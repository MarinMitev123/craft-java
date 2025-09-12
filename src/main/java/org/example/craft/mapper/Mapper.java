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
            : githubUser.getLogin());
    if (githubUser.getEmail() != null && !githubUser.getEmail().isBlank()) {
      freshdeskContact.setEmail(githubUser.getEmail());
    }
    if (githubUser.getLocation() != null && !githubUser.getLocation().isBlank()) {
      freshdeskContact.setAddress(githubUser.getLocation());
    }
    if (githubUser.getTwitterUsername() != null && !githubUser.getTwitterUsername().isBlank()) {
      freshdeskContact.setTwitterId(githubUser.getTwitterUsername());
    }
    return freshdeskContact;
  }
}
