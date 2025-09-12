package org.example.craft.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class GitHubUser {
    private String login;
    private String name;
    private String email;
    private String location;
    private String twitter_username;
    private String created_at;
}
