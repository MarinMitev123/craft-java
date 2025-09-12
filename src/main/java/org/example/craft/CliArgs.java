package org.example.craft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class CliArgs {
  private final String githubUser;
  private final String freshdeskSubdomain;

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
