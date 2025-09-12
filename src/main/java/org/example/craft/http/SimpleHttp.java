package org.example.craft.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public final class SimpleHttp {

    public static final class Response {
        private final int status;
        private final String body;
        public Response(int status, String body) { this.status = status; this.body = body; }
        public int getStatus() { return status; }
        public String getBody() { return body; }
    }

    public interface Transport {
        Response call(String method, String url, Map<String,String> headers, String body) throws Exception;
    }

    public static final class DefaultTransport implements Transport {
        private final HttpClient httpClient = HttpClient.newHttpClient();

        @Override
        public Response call(String method, String url, Map<String,String> headers, String body) throws Exception {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url));
            if (headers != null) {
                for (Map.Entry<String,String> entry : headers.entrySet()) {
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
            HttpResponse<String> httpResponse = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            return new Response(httpResponse.statusCode(), httpResponse.body());
        }
    }
}
