package org.example.craft;

import org.example.craft.freshdesk.FreshdeskClient;
import org.example.craft.freshdesk.dto.FreshdeskContact;
import org.example.craft.http.SimpleHttp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FreshdeskClientTest {

    @Test
    void find_returns_first_match_from_contacts_endpoint() throws Exception {
        String body = "[ { \"id\": 42, \"unique_external_id\": \"github:octo\", \"name\": \"Octo\" } ]";
        SimpleHttp.Transport fakeTransport = (m,u,h,b) -> new SimpleHttp.Response(200, body);

        FreshdeskClient client = new FreshdeskClient(fakeTransport, "sub", "FD");
        FreshdeskContact contact = client.findByExternalId("github:octo");

        assertNotNull(contact);
        assertEquals(42L, contact.getId());
    }

    @Test
    void create_returns_id() throws Exception {
        String body = "{ \"id\": 99 }";
        SimpleHttp.Transport fakeTransport = (m,u,h,b) -> new SimpleHttp.Response(201, body);

        FreshdeskClient client = new FreshdeskClient(fakeTransport, "sub", "FD");
        String id = client.create(new FreshdeskContact());

        assertEquals("99", id);
    }

    @Test
    void update_returns_id() throws Exception {
        String body = "{ \"id\": 77 }";
        SimpleHttp.Transport fakeTransport = (m,u,h,b) -> new SimpleHttp.Response(200, body);

        FreshdeskClient client = new FreshdeskClient(fakeTransport, "sub", "FD");
        String id = client.update("77", new FreshdeskContact());

        assertEquals("77", id);
    }
}
