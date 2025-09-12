package org.example.craft;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        try {
            CliArgs cliArgs = CliArgs.parse(args);
            App application = new App();
            application.run(cliArgs.getGithubUser(), cliArgs.getFreshdeskSubdomain());
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }
}
