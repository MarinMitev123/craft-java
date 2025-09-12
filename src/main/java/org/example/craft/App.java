package org.example.craft;


import org.example.craft.evn.Env;
import org.example.craft.github.GitHubClient;
import org.example.craft.github.dto.GitHubUser;
import org.example.craft.mapper.Mapper;
import org.example.craft.freshdesk.FreshdeskClient;
import org.example.craft.freshdesk.dto.FreshdeskContact;
import org.example.craft.http.SimpleHttp;

public final class App {

    public void run(String githubUsername, String freshdeskSubdomain) throws Exception {
        String githubToken = Env.require("GITHUB_TOKEN");
        String freshdeskToken = Env.require("FRESHDESK_TOKEN");

        SimpleHttp.Transport httpTransport = new SimpleHttp.DefaultTransport();
        GitHubClient githubClient = new GitHubClient(httpTransport, githubToken);
        FreshdeskClient freshdeskClient = new FreshdeskClient(httpTransport, freshdeskSubdomain, freshdeskToken);

        execute(githubUsername, githubClient, freshdeskClient);
    }

    public void execute(String githubUsername, GitHubClient githubClient, FreshdeskClient freshdeskClient) throws Exception {
        GitHubUser githubUser = githubClient.getUser(githubUsername);
        FreshdeskContact freshdeskPayload = Mapper.map(githubUser);

        FreshdeskContact existingContact = freshdeskClient.findByExternalId(freshdeskPayload.getUniqueExternalId());
        if (existingContact == null) {
            String createdId = freshdeskClient.create(freshdeskPayload);
            System.out.println("Created contact #" + createdId + " for " + githubUser.getLogin());
        } else {
            String updatedId = freshdeskClient.update(String.valueOf(existingContact.getId()), freshdeskPayload);
            System.out.println("Updated contact #" + updatedId + " for " + githubUser.getLogin());
        }
    }
}
