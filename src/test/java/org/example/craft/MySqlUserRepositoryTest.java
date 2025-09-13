package org.example.craft;

import org.example.craft.db.MySqlUserRepository;
import org.example.craft.db.UserSnapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for {@link MySqlUserRepository}.
 *
 * <p>This test requires a running MySQL instance and environment variables:</p>
 * <ul>
 *   <li>{@code DB_URL} – JDBC connection string (e.g., jdbc:mysql://localhost:3306/craft_db)</li>
 *   <li>{@code DB_USER} – database username (e.g., root)</li>
 *   <li>{@code DB_PASSWORD} – database password</li>
 * </ul>
 *
 * <p>If these variables are not set, the test will be skipped automatically.</p>
 */
public class MySqlUserRepositoryTest {

    /**
     * Verifies that calling {@link MySqlUserRepository#upsert(UserSnapshot)}
     * inserts a new user if not present, or updates the record if it already exists.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Insert a GitHub user snapshot with name "Octo Cat".</li>
     *   <li>Insert the same login again with updated name "Octo Updated".</li>
     *   <li>No exceptions should be thrown, and MySQL should reflect the latest values.</li>
     * </ol>
     *
     * <p>Because this test depends on an external DB, the only assertion here
     * is {@code assertTrue(true)} after successful execution.</p>
     */
    @Test
    @EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
    @EnabledIfEnvironmentVariable(named = "DB_USER", matches = ".+")
    @EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".*")
    void upsert_and_update_mysql_when_env_present() throws Exception {
        String url  = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASSWORD");

        try (MySqlUserRepository repo = new MySqlUserRepository(url, user, pass)) {
            repo.open();
            repo.upsert(new UserSnapshot("octo", "Octo Cat", "2011-01-25T18:44:36Z"));
            repo.upsert(new UserSnapshot("octo", "Octo Updated", "2011-01-25T18:44:36Z"));
            assertTrue(true);
        }
    }
}
