package org.example.craft;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.example.craft.freshdesk.FreshdeskClient;
import org.example.craft.freshdesk.dto.FreshdeskContact;
import org.example.craft.github.GitHubClient;
import org.example.craft.github.dto.GitHubUser;
import org.junit.jupiter.api.Test;

public class AppFlowTest {

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
