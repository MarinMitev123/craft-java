package org.example.craft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Command-line arguments wrapper.
 * <p>
 * This class is responsible for parsing the CLI arguments passed
 * to the application and exposing them in a type-safe way.
 *
 * <p>Expected usage:</p>
 * <pre>
 *   java -jar craft-java.jar --user octocat --subdomain mycompany
 * </pre>
 *
 * <p>Both parameters are required. If one is missing,
 * an {@link IllegalArgumentException} is thrown.</p>
 */
@Getter
@RequiredArgsConstructor
public final class CliArgs {
  /** GitHub username passed via {@code --user} */
  private final String githubUser;

  /** Freshdesk subdomain passed via {@code --subdomain} */
  private final String freshdeskSubdomain;

  /**
   * Parse CLI arguments into a {@link CliArgs} instance.
   *
   * @param args array of raw command-line arguments
   * @return parsed {@link CliArgs} object
   * @throws IllegalArgumentException if required arguments are missing
   */
  public static CliArgs parse(String[] args) {
    String userArgument = null;
    String subdomainArgument = null;

    for (int index = 0; index < args.length; index++) {
      String argument = args[index];
      if ("--user".equals(argument) && index + 1 < args.length) {
        userArgument = args[++index];
      } else if ("--subdomain".equals(argument) && index + 1 < args.length) {
        subdomainArgument = args[++index];
      }
    }

    if (userArgument == null || subdomainArgument == null) {
      throw new IllegalArgumentException(
              "Usage: --user <github_username> --subdomain <freshdesk_subdomain>");
    }
    return new CliArgs(userArgument, subdomainArgument);
  }
}
