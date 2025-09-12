package org.example.craft;

import org.example.craft.github.dto.GitHubUser;
import org.example.craft.freshdesk.dto.FreshdeskContact;
import org.example.craft.mapper.Mapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTest {

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

  @Test
  void fallback_name_to_login_when_missing() {
    GitHubUser gh = new GitHubUser();
    gh.setLogin("octo"); // name е null

    FreshdeskContact fd = Mapper.map(gh);

    assertEquals("octo", fd.getName());
  }
}
