package org.example.craft.evn;

public final class Env {
  private Env() {}

  public static String require(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Missing env: " + name);
    }
    return value;
  }
}
