package org.example.craft.freshdesk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.example.craft.freshdesk.dto.FreshdeskContact;
import org.example.craft.http.SimpleHttp;

public final class FreshdeskClient {
    private final SimpleHttp.Transport httpTransport;
    private final String freshdeskSubdomain;
    private final String authorizationHeader;
    private final ObjectMapper objectMapper;

    public FreshdeskClient(SimpleHttp.Transport httpTransport, String freshdeskSubdomain, String apiToken) {
        this.httpTransport = httpTransport;
        this.freshdeskSubdomain = freshdeskSubdomain;
        String basic = Base64.getEncoder().encodeToString((apiToken + ":X").getBytes(StandardCharsets.UTF_8));
        this.authorizationHeader = "Basic " + basic;
        this.objectMapper = new ObjectMapper();
    }

    private String baseUrl() {
        return "https://" + freshdeskSubdomain + ".freshdesk.com/api/v2";
    }

    private Map<String,String> jsonHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization", authorizationHeader);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private Map<String,String> authHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization", authorizationHeader);
        return headers;
    }

    public FreshdeskContact findByExternalId(String uniqueExternalId) throws Exception {
        String encodedExternalId = URLEncoder.encode(uniqueExternalId, StandardCharsets.UTF_8);
        String contactsUrl = baseUrl() + "/contacts?unique_external_id=" + encodedExternalId;

        SimpleHttp.Response listResponse = httpTransport.call("GET", contactsUrl, authHeaders(), null);
        if (listResponse.getStatus() == 200) {
            JsonNode arrayNode = objectMapper.readTree(listResponse.getBody());
            if (arrayNode.isArray() && !arrayNode.isEmpty()) {
                return objectMapper.treeToValue(arrayNode.get(0), FreshdeskContact.class);
            }
        }

        // Fallback: Search API
        String searchQuery = URLEncoder.encode("\"unique_external_id:'" + uniqueExternalId + "'\"", StandardCharsets.UTF_8);
        String searchUrl = baseUrl() + "/search/contacts?query=" + searchQuery;
        SimpleHttp.Response searchResponse = httpTransport.call("GET", searchUrl, authHeaders(), null);
        if (searchResponse.getStatus() >= 400) {
            return null;
        }

        JsonNode rootNode = objectMapper.readTree(searchResponse.getBody());
        JsonNode resultsNode = rootNode.get("results");
        if (resultsNode != null && resultsNode.isArray() && resultsNode.size() > 0) {
            return objectMapper.treeToValue(resultsNode.get(0), FreshdeskContact.class);
        }
        return null;
    }

    public String create(FreshdeskContact contact) throws Exception {
        // JSON property names in Freshdesk: unique_external_id, twitter_id
        JsonNode payload = objectMapper.createObjectNode()
                .put("unique_external_id", contact.getUniqueExternalId())
                .put("name", contact.getName());

        if (contact.getEmail() != null) ((com.fasterxml.jackson.databind.node.ObjectNode) payload).put("email", contact.getEmail());
        if (contact.getAddress() != null) ((com.fasterxml.jackson.databind.node.ObjectNode) payload).put("address", contact.getAddress());
        if (contact.getTwitterId() != null) ((com.fasterxml.jackson.databind.node.ObjectNode) payload).put("twitter_id", contact.getTwitterId());

        SimpleHttp.Response response = httpTransport.call(
                "POST",
                baseUrl() + "/contacts",
                jsonHeaders(),
                objectMapper.writeValueAsString(payload)
        );
        if (response.getStatus() >= 400) {
            throw new RuntimeException("Freshdesk create error: " + response.getStatus() + " body=" + response.getBody());
        }
        return objectMapper.readTree(response.getBody()).get("id").asText();
    }

    public String update(String contactId, FreshdeskContact contact) throws Exception {
        JsonNode payload = objectMapper.createObjectNode()
                .put("unique_external_id", contact.getUniqueExternalId())
                .put("name", contact.getName());

        if (contact.getEmail() != null) ((com.fasterxml.jackson.databind.node.ObjectNode) payload).put("email", contact.getEmail());
        if (contact.getAddress() != null) ((com.fasterxml.jackson.databind.node.ObjectNode) payload).put("address", contact.getAddress());
        if (contact.getTwitterId() != null) ((com.fasterxml.jackson.databind.node.ObjectNode) payload).put("twitter_id", contact.getTwitterId());

        SimpleHttp.Response response = httpTransport.call(
                "PUT",
                baseUrl() + "/contacts/" + contactId,
                jsonHeaders(),
                objectMapper.writeValueAsString(payload)
        );
        if (response.getStatus() >= 400) {
            throw new RuntimeException("Freshdesk update error: " + response.getStatus() + " body=" + response.getBody());
        }
        return objectMapper.readTree(response.getBody()).get("id").asText();
    }
}
