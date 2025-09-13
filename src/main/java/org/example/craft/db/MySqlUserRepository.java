package org.example.craft.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class MySqlUserRepository implements AutoCloseable {
    private final String jdbcUrl;
    private final String user;
    private final String password;
    private Connection connection;

    public MySqlUserRepository(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    public void open() throws Exception {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(jdbcUrl, user, password);
            connection.setAutoCommit(true);
        }
    }

    /** По избор – създай таблицата, ако липсва */
    public void initSchema() throws Exception {
        try (var st = connection.createStatement()) {
            st.execute("""
        CREATE TABLE IF NOT EXISTS github_users (
          login VARCHAR(100) PRIMARY KEY,
          name VARCHAR(200),
          created_at VARCHAR(50)
        )
      """);
        }
    }

    /** INSERT ... ON DUPLICATE KEY UPDATE по PK=login */
    public void upsert(UserSnapshot snapshot) throws Exception {
        System.out.println("[DBG] UPSERT login=" + snapshot.getLogin()
                + ", name=" + snapshot.getName()
                + ", createdAt=" + snapshot.getCreatedAt());

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO github_users(login, name, createdAt) " +
                        "VALUES(?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "name = VALUES(name), " +
                        "created_at = VALUES(created_at)"
        )) {
            ps.setString(1, snapshot.getLogin());
            ps.setString(2, snapshot.getName());
            ps.setString(3, snapshot.getCreatedAt());
            ps.executeUpdate();
        }
    }

    public UserSnapshot findByLogin(String login) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT login, name, created_at FROM github_users WHERE login = ?"
        )) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserSnapshot(
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getString("created_at"));
                }
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
