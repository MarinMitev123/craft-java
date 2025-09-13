package org.example.craft.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.craft.github.dto.GitHubUser;
import org.example.craft.http.SimpleHttp;

import java.util.HashMap;
import java.util.Map;

/**
 * Client for GitHub REST API v3.
 * <p>
 * Provides methods to fetch public information about GitHub users
 * using the /users/{username} endpoint.
 */
@Getter
@RequiredArgsConstructor
public final class GitHubClient {
  private final SimpleHttp.Transport httpTransport;
  private final String githubToken;
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Retrieves information about a GitHub user by username.
   * <p>
   * Sends a GET request to {@code https://api.github.com/users/{username}} with
   * authorization and required headers. The response is deserialized into a {@link GitHubUser}.
   *
   * @param username the GitHub login (e.g., "octocat")
   * @return the {@link GitHubUser} object containing user details
   * @throws RuntimeException if the user is not found (404) or another API error occurs (4xx/5xx)
   * @throws Exception if a network, I/O, or JSON parsing error occurs
   */
  public GitHubUser getUser(String username) throws Exception {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + githubToken); // "Bearer" or "token" both work
    headers.put("Accept", "application/vnd.github+json");
    headers.put("X-GitHub-Api-Version", "2022-11-28");

    SimpleHttp.Response response =
            httpTransport.call("GET", "https://api.github.com/users/" + username, headers, null);

    if (response.getStatus() == 404) {
      throw new RuntimeException("GitHub user not found: " + username);
    }
    if (response.getStatus() >= 400) {
      throw new RuntimeException(
              "GitHub error: " + response.getStatus() + " body=" + response.getBody());
    }

    GitHubUser u = objectMapper.readValue(response.getBody(), GitHubUser.class);
    System.out.println("[DBG] GitHub user: login=" + u.getLogin() + ", createdAt=" + u.getCreatedAt());
    return u;
  }
}
