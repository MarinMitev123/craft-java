package org.example.craft.evn;

/**
 * Utility class for accessing environment variables in a safe way.
 * <p>
 * Provides helper methods to retrieve required environment variables and
 * throws an exception if they are missing or empty.
 */
public final class Env {

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private Env() {}

  /**
   * Retrieves the value of the given environment variable.
   *
   * @param name the name of the environment variable (e.g., "GITHUB_TOKEN")
   * @return the value of the environment variable
   * @throws IllegalStateException if the environment variable is not set or is blank
   */
  public static String require(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Missing env: " + name);
    }
    return value;
  }
}
