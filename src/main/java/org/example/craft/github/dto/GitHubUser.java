package org.example.craft.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for the GitHub /users/{username} API response.
 * <p>
 * This class maps only the fields that are relevant for the application.
 * Jackson annotations are used to correctly map JSON snake_case properties
 * into Java camelCase fields.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class GitHubUser {

  /** The GitHub login (username). Always present. */
  private String login;

  /** The display name of the user. May be {@code null}. */
  private String name;

  /**
   * The creation date of the GitHub account.
   * Example: {@code 2011-01-25T18:44:36Z}.
   */
  @JsonProperty("created_at")
  private String createdAt;

  /** The public email address of the user, if available. */
  private String email;

  /** The location of the user, if set in their profile. */
  private String location;

  /** The Twitter handle of the user, if linked. */
  @JsonProperty("twitter_username")
  private String twitterUsername;
}
