package org.example.craft.freshdesk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.example.craft.freshdesk.dto.FreshdeskContact;
import org.example.craft.http.SimpleHttp;

/**
 * Client for interacting with the Freshdesk API v2.
 * <p>
 * Supports creating, updating, and finding contacts by their unique external ID.
 * Uses basic authentication with an API token.
 */
public final class FreshdeskClient {
  private final SimpleHttp.Transport httpTransport;
  private final String freshdeskSubdomain;
  private final String authorizationHeader;
  private final ObjectMapper objectMapper;

  /**
   * Constructs a new Freshdesk client.
   *
   * @param httpTransport        the HTTP transport to use (for real calls or test doubles)
   * @param freshdeskSubdomain   the Freshdesk subdomain (e.g., "mycompany")
   * @param apiToken             the Freshdesk API token
   */
  public FreshdeskClient(
          SimpleHttp.Transport httpTransport, String freshdeskSubdomain, String apiToken) {
    this.httpTransport = httpTransport;
    this.freshdeskSubdomain = freshdeskSubdomain;
    String basic =
            Base64.getEncoder().encodeToString((apiToken + ":X").getBytes(StandardCharsets.UTF_8));
    this.authorizationHeader = "Basic " + basic;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Builds the Freshdesk API base URL.
   *
   * @return base API URL (e.g., https://subdomain.freshdesk.com/api/v2)
   */
  private String baseUrl() {
    return "https://" + freshdeskSubdomain + ".freshdesk.com/api/v2";
  }

  /**
   * Builds headers for JSON requests (Authorization + Content-Type).
   *
   * @return headers map for POST/PUT requests
   */
  private Map<String, String> jsonHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", authorizationHeader);
    headers.put("Content-Type", "application/json");
    return headers;
  }

  /**
   * Builds headers for authenticated GET requests (Authorization only).
   *
   * @return headers map for GET requests
   */
  private Map<String, String> authHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", authorizationHeader);
    return headers;
  }

  /**
   * Finds a contact by unique external ID.
   * <p>
   * Uses the /contacts endpoint first, then falls back to the Search API.
   *
   * @param uniqueExternalId external ID to search by
   * @return matching {@link FreshdeskContact}, or {@code null} if not found
   * @throws Exception if a network or JSON parsing error occurs
   */
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
    String searchQuery =
            URLEncoder.encode(
                    "\"unique_external_id:'" + uniqueExternalId + "'\"", StandardCharsets.UTF_8);
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

  /**
   * Creates a new Freshdesk contact.
   *
   * @param contact contact details to create
   * @return ID of the newly created contact
   * @throws Exception if the API call fails or JSON processing fails
   */
  public String create(FreshdeskContact contact) throws Exception {
    ObjectNode payload =
            objectMapper
                    .createObjectNode()
                    .put("unique_external_id", contact.getUniqueExternalId())
                    .put("name", contact.getName());

    if (contact.getEmail() != null) {
      payload.put("email", contact.getEmail());
    }
    if (contact.getAddress() != null) {
      payload.put("address", contact.getAddress());
    }
    if (contact.getTwitterId() != null) {
      payload.put("twitter_id", contact.getTwitterId());
    }

    SimpleHttp.Response response =
            httpTransport.call(
                    "POST",
                    baseUrl() + "/contacts",
                    jsonHeaders(),
                    objectMapper.writeValueAsString(payload));
    if (response.getStatus() >= 400) {
      throw new RuntimeException(
              "Freshdesk create error: " + response.getStatus() + " body=" + response.getBody());
    }
    return objectMapper.readTree(response.getBody()).get("id").asText();
  }

  /**
   * Updates an existing Freshdesk contact.
   *
   * @param contactId ID of the contact to update
   * @param contact   new contact data
   * @return ID of the updated contact
   * @throws Exception if the API call fails or JSON processing fails
   */
  public String update(String contactId, FreshdeskContact contact) throws Exception {
    ObjectNode payload =
            objectMapper
                    .createObjectNode()
                    .put("unique_external_id", contact.getUniqueExternalId())
                    .put("name", contact.getName());

    if (contact.getEmail() != null) {
      payload.put("email", contact.getEmail());
    }
    if (contact.getAddress() != null) {
      payload.put("address", contact.getAddress());
    }
    if (contact.getTwitterId() != null) {
      payload.put("twitter_id", contact.getTwitterId());
    }

    SimpleHttp.Response response =
            httpTransport.call(
                    "PUT",
                    baseUrl() + "/contacts/" + contactId,
                    jsonHeaders(),
                    objectMapper.writeValueAsString(payload));
    if (response.getStatus() >= 400) {
      throw new RuntimeException(
              "Freshdesk update error: " + response.getStatus() + " body=" + response.getBody());
    }
    return objectMapper.readTree(response.getBody()).get("id").asText();
  }
}
