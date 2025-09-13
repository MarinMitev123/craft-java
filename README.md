# Craft Java Project
This is a command-line program written in Java 17. It retrieves information about a GitHub user and creates or updates a Freshdesk contact using their APIs, and optionally persists user data in a MySQL database.
## Requirements
Java 17+, Maven 3.9+, GitHub Personal Access Token (set in GITHUB_TOKEN), Freshdesk API Key (set in FRESHDESK_TOKEN), Freshdesk subdomain (for example, if your portal is https://mycompany.freshdesk.com, the subdomain is "mycompany"). Optional: MySQL database connection with environment variables DB_URL (e.g., jdbc:mysql://localhost:3306/craft_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC), DB_USER (e.g., root), DB_PASSWORD (your password).
## Build
To build the project run: `mvn clean package`. This will generate a runnable JAR file in `target/craft-java-1.0.0.jar`.
## Environment Variables
On Windows (PowerShell):  
`$env:GITHUB_TOKEN = "your_github_pat"`  
`$env:FRESHDESK_TOKEN = "your_freshdesk_api_key"`  
`$env:DB_URL = "jdbc:mysql://localhost:3306/craft_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"`  
`$env:DB_USER = "root"`  
`$env:DB_PASSWORD = "your_password"`  
On Linux / macOS:  
`export GITHUB_TOKEN=your_github_pat`  
`export FRESHDESK_TOKEN=your_freshdesk_api_key`  
`export DB_URL="jdbc:mysql://localhost:3306/craft_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"`  
`export DB_USER=root`  
`export DB_PASSWORD=your_password`
## Run
Run the program with:  
`java -jar target/craft-java-1.0.0.jar --user <github_username> --subdomain <freshdesk_subdomain>`  
Example:  
`java -jar target/craft-java-1.0.0.jar --user octocat --subdomain mycompany`
## Features
Fetches user details from GitHub REST API v3. Maps GitHub fields to Freshdesk contact: login → unique_external_id, name → name (falls back to login if missing), email → email, location → address, twitter_username → twitter_id. Creates or updates the contact in Freshdesk. Optionally persists login, name, and creation date into a MySQL database (github_users table).
## Example Output
`Created contact #206001870104 for MarinMitev123`  
or  
`Updated contact #206001870104 for MarinMitev123`
## Tests
Run all unit tests with `mvn test`. Includes: GitHubClientTest (verifies GitHub API parsing), FreshdeskClientTest (verifies create/update logic), MapperTest (verifies mapping GitHub → Freshdesk), AppFlowTest (verifies overall flow), MySqlUserRepositoryTest (verifies persistence, requires DB env variables).
## Database Schema
If using MySQL, make sure the following table exists:  
`CREATE TABLE IF NOT EXISTS github_users ( login VARCHAR(255) PRIMARY KEY, name VARCHAR(255) NOT NULL, created_at VARCHAR(64) NOT NULL );`

