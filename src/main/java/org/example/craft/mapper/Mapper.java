package org.example.craft.mapper;

import org.example.craft.github.dto.GitHubUser;
import org.example.craft.freshdesk.dto.FreshdeskContact;

public final class Mapper {
    private Mapper() {}

    public static FreshdeskContact map(GitHubUser githubUser) {
        FreshdeskContact freshdeskContact = new FreshdeskContact();
        freshdeskContact.setUniqueExternalId("github:" + githubUser.getLogin());
        freshdeskContact.setName(
                githubUser.getName() != null && !githubUser.getName().isBlank()
                        ? githubUser.getName()
                        : githubUser.getLogin()
        );
        if (githubUser.getEmail() != null && !githubUser.getEmail().isBlank()) {
            freshdeskContact.setEmail(githubUser.getEmail());
        }
        if (githubUser.getLocation() != null && !githubUser.getLocation().isBlank()) {
            freshdeskContact.setAddress(githubUser.getLocation());
        }
        if (githubUser.getTwitter_username() != null && !githubUser.getTwitter_username().isBlank()) {
            freshdeskContact.setTwitterId(githubUser.getTwitter_username());
        }
        return freshdeskContact;
    }
}
