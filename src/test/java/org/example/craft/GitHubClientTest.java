package org.example.craft;

import org.example.craft.github.GitHubClient;
import org.example.craft.github.dto.GitHubUser;
import org.example.craft.http.SimpleHttp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GitHubClient}.
 *
 * <p>These tests inject a fake {@link SimpleHttp.Transport} that simulates GitHub API
 * responses without making real HTTP calls.</p>
 */
public class GitHubClientTest {

  /**
   * Verifies that {@link GitHubClient#getUser(String)} correctly parses
   * the JSON body into a {@link GitHubUser} object.
   *
   * <p>Input: HTTP 200 with JSON containing {@code login} and {@code name}.</p>
   * <p>Expected: {@link GitHubUser#getLogin()} and {@link GitHubUser#getName()} match values.</p>
   */
  @Test
  void parses_user() throws Exception {
    SimpleHttp.Transport fakeTransport =
            (method, url, headers, body) ->
                    new SimpleHttp.Response(200, "{ \"login\": \"octo\", \"name\": \"Octo Cat\" }");

    GitHubClient client = new GitHubClient(fakeTransport, "T");
    GitHubUser user = client.getUser("octo");

    assertEquals("octo", user.getLogin());
    assertEquals("Octo Cat", user.getName());
  }

  /**
   * Verifies that {@link GitHubClient#getUser(String)} throws an exception
   * when the API returns 404 (user not found).
   *
   * <p>Input: HTTP 404 with empty JSON body.</p>
   * <p>Expected: {@link RuntimeException} is thrown.</p>
   */
  @Test
  void not_found_throws() {
    SimpleHttp.Transport fakeTransport =
            (method, url, headers, body) -> new SimpleHttp.Response(404, "{}");

    GitHubClient client = new GitHubClient(fakeTransport, "T");
    assertThrows(RuntimeException.class, () -> client.getUser("nope"));
  }
}
