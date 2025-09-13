package org.example.craft;

import org.example.craft.github.dto.GitHubUser;
import org.example.craft.freshdesk.dto.FreshdeskContact;
import org.example.craft.mapper.Mapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Mapper}.
 *
 * <p>The {@link Mapper#map(GitHubUser)} method is responsible for converting
 * a GitHub user DTO into a Freshdesk contact DTO with matching fields.</p>
 */
public class MapperTest {

  /**
   * Verifies that a {@link GitHubUser} with all fields populated
   * is correctly mapped to a {@link FreshdeskContact}.
   *
   * <p>Expected mappings:</p>
   * <ul>
   *   <li>{@code login → uniqueExternalId (prefixed with "github:")}</li>
   *   <li>{@code name → name}</li>
   *   <li>{@code email → email}</li>
   *   <li>{@code location → address}</li>
   *   <li>{@code twitterUsername → twitterId}</li>
   * </ul>
   */
  @Test
  void maps_basic_fields() {
    GitHubUser gh = new GitHubUser();
    gh.setLogin("octo");
    gh.setName("Octo Cat");
    gh.setEmail("o@gh");
    gh.setLocation("Sofia");
    gh.setTwitterUsername("octo");

    FreshdeskContact fd = Mapper.map(gh);

    assertEquals("github:octo", fd.getUniqueExternalId());
    assertEquals("Octo Cat", fd.getName());
    assertEquals("o@gh", fd.getEmail());
    assertEquals("Sofia", fd.getAddress());
    assertEquals("octo", fd.getTwitterId());
  }

  /**
   * Verifies that if {@code name} is missing in GitHub user,
   * the {@code login} value is used as a fallback for the Freshdesk contact name.
   */
  @Test
  void fallback_name_to_login_when_missing() {
    GitHubUser gh = new GitHubUser();
    gh.setLogin("octo"); // name is null

    FreshdeskContact fd = Mapper.map(gh);

    assertEquals("octo", fd.getName());
  }
}
