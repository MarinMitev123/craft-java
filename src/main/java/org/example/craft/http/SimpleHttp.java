package org.example.craft.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Minimal HTTP utility with pluggable {@link Transport} interface.
 * <p>
 * Provides a simple abstraction over Java's {@link HttpClient} for executing GET, POST, and PUT requests.
 * Useful for decoupling HTTP logic from API clients and simplifying testing.
 */
public final class SimpleHttp {

  /**
   * Immutable HTTP response representation.
   */
  @Getter
  @RequiredArgsConstructor
  public static final class Response {
    /** HTTP status code (e.g., 200, 404, 500). */
    private final int status;

    /** Response body as plain text. */
    private final String body;
  }

  /**
   * Transport abstraction for executing HTTP calls.
   * <p>
   * Allows injecting custom implementations (e.g., mocks in tests).
   */
  public interface Transport {
    /**
     * Executes an HTTP request.
     *
     * @param method  HTTP method (e.g., "GET", "POST", "PUT")
     * @param url     full target URL
     * @param headers request headers (may be empty), case-sensitive keys
     * @param body    request body or {@code null} for requests without a body
     * @return {@link Response} containing status code and response body as text
     * @throws Exception if a network, timeout, or I/O error occurs
     */
    Response call(String method, String url, Map<String, String> headers, String body) throws Exception;
  }

  /**
   * Default implementation of {@link Transport} using Java's {@link HttpClient}.
   */
  public static final class DefaultTransport implements Transport {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Executes an HTTP request using {@link HttpClient}.
     *
     * @param method  HTTP method (only GET, POST, PUT are supported)
     * @param url     full target URL
     * @param headers request headers (may be empty), case-sensitive keys
     * @param body    request body or {@code null} for requests without a body
     * @return {@link Response} containing status code and response body as text
     * @throws Exception if a network, timeout, or I/O error occurs
     * @throws IllegalArgumentException if an unsupported method is provided
     */
    @Override
    public Response call(String method, String url, Map<String, String> headers, String body)
            throws Exception {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url));
      if (headers != null) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          requestBuilder.header(entry.getKey(), entry.getValue());
        }
      }
      if ("GET".equals(method)) {
        requestBuilder.GET();
      } else if ("POST".equals(method)) {
        requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
      } else if ("PUT".equals(method)) {
        requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
      } else {
        throw new IllegalArgumentException("Unsupported method: " + method);
      }
      HttpResponse<String> httpResponse =
              httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
      return new Response(httpResponse.statusCode(), httpResponse.body());
    }
  }
}
