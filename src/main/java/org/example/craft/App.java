package org.example.craft;

import org.example.craft.evn.Env;
import org.example.craft.github.GitHubClient;
import org.example.craft.github.dto.GitHubUser;
import org.example.craft.mapper.Mapper;
import org.example.craft.freshdesk.FreshdeskClient;
import org.example.craft.freshdesk.dto.FreshdeskContact;
import org.example.craft.http.SimpleHttp;
import org.example.craft.db.MySqlUserRepository;
import org.example.craft.db.UserSnapshot;

/**
 * Main application orchestrator that ties together:
 * <ul>
 *   <li>GitHub API (fetching user information)</li>
 *   <li>MySQL database persistence (storing user snapshots)</li>
 *   <li>Freshdesk API (creating/updating contacts)</li>
 * </ul>
 *
 * <p>Execution flow:</p>
 * <ol>
 *   <li>Load required environment variables (tokens and DB credentials).</li>
 *   <li>Initialize HTTP clients for GitHub and Freshdesk.</li>
 *   <li>Open MySQL repository and ensure schema exists.</li>
 *   <li>Fetch GitHub user by login.</li>
 *   <li>Persist user in MySQL.</li>
 *   <li>Create or update Freshdesk contact mapped from GitHub user.</li>
 * </ol>
 */
public final class App {

  /**
   * Entry point for normal execution with MySQL enabled.
   *
   * @param githubUsername     GitHub login (e.g., "octocat")
   * @param freshdeskSubdomain Freshdesk subdomain (e.g., "mycompany")
   * @throws Exception if any I/O, DB, or API operation fails
   */
  public void run(String githubUsername, String freshdeskSubdomain) throws Exception {
    // Required environment variables
    String githubToken    = Env.require("GITHUB_TOKEN");
    String freshdeskToken = Env.require("FRESHDESK_TOKEN");

    // MySQL credentials
    String dbUrl      = Env.require("DB_URL");
    String dbUser     = Env.require("DB_USER");
    String dbPassword = Env.require("DB_PASSWORD");

    // Initialize HTTP + API clients
    SimpleHttp.Transport httpTransport = new SimpleHttp.DefaultTransport();
    GitHubClient githubClient    = new GitHubClient(httpTransport, githubToken);
    FreshdeskClient freshdeskClient = new FreshdeskClient(httpTransport, freshdeskSubdomain, freshdeskToken);

    try (MySqlUserRepository repo = new MySqlUserRepository(dbUrl, dbUser, dbPassword)) {
      repo.open();
      repo.initSchema(); // auto-create table if missing

      // 1. GitHub → user
      GitHubUser githubUser = githubClient.getUser(githubUsername);

      // 2. Persist in MySQL
      persistUser(repo, githubUser);

      // 3. Freshdesk create/update
      FreshdeskContact payload = Mapper.map(githubUser);
      FreshdeskContact existing = freshdeskClient.findByExternalId(payload.getUniqueExternalId());
      if (existing == null) {
        String id = freshdeskClient.create(payload);
        System.out.println("Created contact #" + id + " for " + githubUser.getLogin());
      } else {
        String id = freshdeskClient.update(String.valueOf(existing.getId()), payload);
        System.out.println("Updated contact #" + id + " for " + githubUser.getLogin());
      }
    }
  }

  /**
   * Alternative entry point for testing or "no DB" mode.
   * <p>
   * Performs GitHub → Freshdesk sync without persisting to MySQL.
   * Useful in unit tests.
   *
   * @param githubUsername GitHub login
   * @param githubClient   GitHub API client
   * @param freshdeskClient Freshdesk API client
   * @throws Exception if any I/O or API operation fails
   */
  public void execute(String githubUsername, GitHubClient githubClient, FreshdeskClient freshdeskClient) throws Exception {
    GitHubUser githubUser = githubClient.getUser(githubUsername);
    FreshdeskContact payload = Mapper.map(githubUser);
    FreshdeskContact existing = freshdeskClient.findByExternalId(payload.getUniqueExternalId());
    if (existing == null) {
      String id = freshdeskClient.create(payload);
      System.out.println("Created contact #" + id + " for " + githubUser.getLogin());
    } else {
      String id = freshdeskClient.update(String.valueOf(existing.getId()), payload);
      System.out.println("Updated contact #" + id + " for " + githubUser.getLogin());
    }
  }

  /**
   * Persists a {@link GitHubUser} into MySQL as a {@link UserSnapshot}.
   *
   * @param repo        MySQL repository
   * @param githubUser  GitHub user to persist
   * @throws Exception if SQL operation fails
   */
  private static void persistUser(MySqlUserRepository repo, GitHubUser githubUser) throws Exception {
    String login = githubUser.getLogin();
    String name = (githubUser.getName() != null && !githubUser.getName().isBlank())
            ? githubUser.getName() : githubUser.getLogin();
    String createdAt = githubUser.getCreatedAt();

    System.out.println("[DBG] Persisting: login=" + login + ", name=" + name + ", createdAt=" + createdAt);
    repo.upsert(new UserSnapshot(login, name, createdAt));
  }
}
