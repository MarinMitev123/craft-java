

This is a command-line program written in Java 17. It retrieves information about a GitHub user and creates or updates a Freshdesk contact using their APIs. The program requires Java 17+, Maven 3.9+, a GitHub Personal Access Token stored in the environment variable GITHUB_TOKEN, and a Freshdesk API key stored in the environment variable FRESHDESK_TOKEN. The Freshdesk subdomain must also be provided when running the program (for example, if your portal is https://mycompany.freshdesk.com, then the subdomain is "mycompany").

To build the project run:  
`mvn clean package -DskipTests`  
This will generate a runnable JAR in `target/craft-java-1.0.0.jar`.

Before running the program you must set environment variables.  
On Windows (PowerShell):  
`$env:GITHUB_TOKEN = "your_github_pat"`  
`$env:FRESHDESK_TOKEN = "your_freshdesk_api_key"`

On Linux / macOS:  
`export GITHUB_TOKEN=your_github_pat`  
`export FRESHDESK_TOKEN=your_freshdesk_api_key`

To run the program use:  
`java -jar target/craft-java-1.0.0.jar --user <github_username> --subdomain <freshdesk_subdomain>`  
Example:  
`java -jar target/craft-java-1.0.0.jar --user octocat --subdomain mycompany`

The program fetches GitHub user details via the GitHub REST API v3, maps them to a Freshdesk contact (fields: name, email, address, twitter_id, unique_external_id), and then either creates the contact if it does not exist or updates it if it already exists.

Unit tests are included and can be run with:  
`mvn test`

Example output:  
`Created contact #206001870104 for MarinMitev123`  
or  
`Updated contact #206001870104 for MarinMitev123`
