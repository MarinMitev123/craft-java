package org.example.craft;

import org.example.craft.github.GitHubClient;
import org.example.craft.github.dto.GitHubUser;
import org.example.craft.http.SimpleHttp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GitHubClientTest {

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

  @Test
  void not_found_throws() {
    SimpleHttp.Transport fakeTransport =
        (method, url, headers, body) -> new SimpleHttp.Response(404, "{}");

    GitHubClient client = new GitHubClient(fakeTransport, "T");
    assertThrows(RuntimeException.class, () -> client.getUser("nope"));
  }
}
