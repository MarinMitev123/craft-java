package org.example.craft.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Immutable-like DTO for persistence */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UserSnapshot {
    private String login;
    private String name;
    private String createdAt;
}
