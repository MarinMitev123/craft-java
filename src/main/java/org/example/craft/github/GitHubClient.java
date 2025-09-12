package org.example.craft.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.example.craft.github.dto.GitHubUser;
import org.example.craft.http.SimpleHttp;

public final class GitHubClient {
  private final SimpleHttp.Transport httpTransport;
  private final String githubToken;
  private final ObjectMapper objectMapper;

  public GitHubClient(SimpleHttp.Transport httpTransport, String githubToken) {
    this.httpTransport = httpTransport;
    this.githubToken = githubToken;
    this.objectMapper = new ObjectMapper();
  }

  public GitHubUser getUser(String username) throws Exception {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "token " + githubToken);
    headers.put("Accept", "application/vnd.github+json");

    SimpleHttp.Response response =
        httpTransport.call("GET", "https://api.github.com/users/" + username, headers, null);

    if (response.getStatus() == 404) {
      throw new RuntimeException("GitHub user not found");
    }
    if (response.getStatus() >= 400) {
      throw new RuntimeException(
          "GitHub error: " + response.getStatus() + " body=" + response.getBody());
    }
    return objectMapper.readValue(response.getBody(), GitHubUser.class);
  }
}
