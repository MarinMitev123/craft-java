package org.example.craft;

import org.example.craft.freshdesk.FreshdeskClient;
import org.example.craft.freshdesk.dto.FreshdeskContact;
import org.example.craft.http.SimpleHttp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FreshdeskClient}.
 *
 * <p>All tests inject a "fake" {@link SimpleHttp.Transport} that immediately
 * returns a pre-canned response, avoiding real HTTP calls to Freshdesk.
 */
public class FreshdeskClientTest {

  /**
   * Verifies that {@link FreshdeskClient#findByExternalId(String)}
   * correctly deserializes the first contact from the JSON array response.
   *
   * <p>Input: HTTP 200 with body containing an array of one object.</p>
   * <p>Expected: non-null {@link FreshdeskContact} with matching {@code id}.</p>
   */
  @Test
  void find_returns_first_match_from_contacts_endpoint() throws Exception {
    String body = "[ { \"id\": 42, \"unique_external_id\": \"github:octo\", \"name\": \"Octo\" } ]";
    SimpleHttp.Transport fakeTransport = (m, u, h, b) -> new SimpleHttp.Response(200, body);

    FreshdeskClient client = new FreshdeskClient(fakeTransport, "sub", "FD");
    FreshdeskContact contact = client.findByExternalId("github:octo");

    assertNotNull(contact);
    assertEquals(42L, contact.getId());
  }

  /**
   * Verifies that {@link FreshdeskClient#create(FreshdeskContact)}
   * returns the {@code id} field from a JSON success response.
   *
   * <p>Input: HTTP 201 with JSON body containing an {@code id}.</p>
   * <p>Expected: returned id equals "99".</p>
   */
  @Test
  void create_returns_id() throws Exception {
    String body = "{ \"id\": 99 }";
    SimpleHttp.Transport fakeTransport = (m, u, h, b) -> new SimpleHttp.Response(201, body);

    FreshdeskClient client = new FreshdeskClient(fakeTransport, "sub", "FD");
    String id = client.create(new FreshdeskContact());

    assertEquals("99", id);
  }

  /**
   * Verifies that {@link FreshdeskClient#update(String, FreshdeskContact)}
   * returns the {@code id} field from a JSON success response.
   *
   * <p>Input: HTTP 200 with JSON body containing an {@code id}.</p>
   * <p>Expected: returned id equals "77".</p>
   */
  @Test
  void update_returns_id() throws Exception {
    String body = "{ \"id\": 77 }";
    SimpleHttp.Transport fakeTransport = (m, u, h, b) -> new SimpleHttp.Response(200, body);

    FreshdeskClient client = new FreshdeskClient(fakeTransport, "sub", "FD");
    String id = client.update("77", new FreshdeskContact());

    assertEquals("77", id);
  }
}
