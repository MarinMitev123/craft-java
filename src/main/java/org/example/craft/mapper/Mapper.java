package org.example.craft.mapper;

import org.example.craft.github.dto.GitHubUser;
import org.example.craft.freshdesk.dto.FreshdeskContact;

/**
 * Utility class responsible for mapping between different domain models.
 * <p>
 * Specifically, it converts a {@link GitHubUser} (fetched from the GitHub API)
 * into a {@link FreshdeskContact} (used in the Freshdesk API).
 */
public final class Mapper {

  /** Private constructor to prevent instantiation (utility class). */
  private Mapper() {}

  /**
   * Maps a {@link GitHubUser} into a {@link FreshdeskContact}.
   * <p>
   * Rules applied:
   * <ul>
   *     <li>{@code uniqueExternalId} is generated as {@code "github:" + login}.</li>
   *     <li>{@code name} is taken from GitHub name if available, otherwise the GitHub login.</li>
   *     <li>{@code email}, {@code address}, and {@code twitterId} are set only if not blank.</li>
   * </ul>
   *
   * @param githubUser GitHub user object returned from the GitHub API
   * @return Freshdesk contact ready to be created/updated in Freshdesk
   */
  public static FreshdeskContact map(GitHubUser githubUser) {
    FreshdeskContact freshdeskContact = new FreshdeskContact();

    // External ID ensures uniqueness and links GitHub user to Freshdesk contact
    freshdeskContact.setUniqueExternalId("github:" + githubUser.getLogin());

    // Prefer GitHub "name"; fallback to "login" if missing
    freshdeskContact.setName(
            githubUser.getName() != null && !githubUser.getName().isBlank()
                    ? githubUser.getName()
                    : githubUser.getLogin()
    );

    // Optional mappings
    if (githubUser.getEmail() != null && !githubUser.getEmail().isBlank()) {
      freshdeskContact.setEmail(githubUser.getEmail());
    }
    if (githubUser.getLocation() != null && !githubUser.getLocation().isBlank()) {
      freshdeskContact.setAddress(githubUser.getLocation());
    }
    if (githubUser.getTwitterUsername() != null && !githubUser.getTwitterUsername().isBlank()) {
      freshdeskContact.setTwitterId(githubUser.getTwitterUsername());
    }

    return freshdeskContact;
  }
}
