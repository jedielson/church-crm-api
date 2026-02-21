# organization Specification Delta

## MODIFIED Requirements

### Requirement: Church Creation with Admin User
The system SHALL create an admin user automatically when a church is created, assigned to the ADMIN group.

#### Scenario: Admin user creation during church setup
Given a church creation request with user details
When the church is successfully created
Then an admin user is automatically created in Keycloak with hardcoded password "password"
And assigned to the ADMIN group
And stored in the Users table with organization ID relationship
And receives a default password requiring reset on first access
And belongs to the created church's organization