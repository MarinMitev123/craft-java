package org.example.craft;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.example.craft.freshdesk.FreshdeskClient;
import org.example.craft.freshdesk.dto.FreshdeskContact;
import org.example.craft.github.GitHubClient;
import org.example.craft.github.dto.GitHubUser;
import org.junit.jupiter.api.Test;

/**
 * Integration-style flow tests for {@link App#execute(String, GitHubClient, FreshdeskClient)}.
 *
 * <p>These tests validate the "decision logic" of the App:
 * <ul>
 *   <li>If a GitHub user has no corresponding Freshdesk contact → create one.</li>
 *   <li>If a GitHub user already has a Freshdesk contact → update it.</li>
 * </ul>
 *
 * <p>Real HTTP or database calls are replaced with Mockito mocks,
 * so we only verify the control flow and mapping.</p>
 */
public class AppFlowTest {

    /**
     * Scenario: Freshdesk has no existing contact for this GitHub user.
     * <p>
     * Expected: {@link FreshdeskClient#create(FreshdeskContact)} is called,
     * and {@link FreshdeskClient#update(String, FreshdeskContact)} is never called.
     */
    @Test
    void creates_when_not_found() throws Exception {
        GitHubClient githubClient = mock(GitHubClient.class);
        FreshdeskClient freshdeskClient = mock(FreshdeskClient.class);

        GitHubUser githubUser = new GitHubUser();
        githubUser.setLogin("octo");
        githubUser.setName("Octo");
        when(githubClient.getUser("octo")).thenReturn(githubUser);

        when(freshdeskClient.findByExternalId("github:octo")).thenReturn(null);
        when(freshdeskClient.create(any())).thenReturn("123");

        new App().execute("octo", githubClient, freshdeskClient);

        verify(freshdeskClient).create(argThat(contact ->
                "github:octo".equals(contact.getUniqueExternalId()) &&
                        "Octo".equals(contact.getName())
        ));
        verify(freshdeskClient, never()).update(anyString(), any());
    }

    /**
     * Scenario: Freshdesk already contains a contact with the same GitHub external ID.
     * <p>
     * Expected: {@link FreshdeskClient#update(String, FreshdeskContact)} is called,
     * and {@link FreshdeskClient#create(FreshdeskContact)} is never called.
     */
    @Test
    void updates_when_found() throws Exception {
        GitHubClient githubClient = mock(GitHubClient.class);
        FreshdeskClient freshdeskClient = mock(FreshdeskClient.class);

        GitHubUser githubUser = new GitHubUser();
        githubUser.setLogin("octo");
        githubUser.setName("Octo");
        when(githubClient.getUser("octo")).thenReturn(githubUser);

        FreshdeskContact existing = new FreshdeskContact();
        existing.setId(777L);
        existing.setUniqueExternalId("github:octo");
        when(freshdeskClient.findByExternalId("github:octo")).thenReturn(existing);
        when(freshdeskClient.update(eq("777"), any())).thenReturn("777");

        new App().execute("octo", githubClient, freshdeskClient);

        verify(freshdeskClient).update(eq("777"), any());
        verify(freshdeskClient, never()).create(any());
    }
}
