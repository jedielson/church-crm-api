# identity Specification

## Purpose
Provides identity management capabilities within church organizations, allowing creation and management of users with 
organization-scoped access control.

## ADDED Requirements

### Requirement: User Creation Capability
The system SHALL allow creation of new users within an organization via HTTP endpoint.

#### Scenario: User creation via HTTP endpoint
Given a valid user creation request with username, fullname, email
When the endpoint is called by an authorized admin user
Then a new user is created in Keycloak with hardcoded password "password"
And assigned to the USERS group
And stored in the Users table with organization ID relationship
And receives a default password requiring reset on first access

### Requirement: User Creation Response
The system SHALL return the created user data upon successful creation.

#### Scenario: User creation response
Given a successful user creation request
When the user is created
Then the system returns HTTP 201 with UserDto in the response body
And the Location header points to the user details endpoint

### Requirement: User Validation
User inputs SHALL meet validation requirements for username, fullname, and email.

#### Scenario: User input validation
Given user creation input
Then username must be 3-50 characters and not blank
And fullname must be 3-200 characters and not blank
And email must be valid format, max 254 characters, and not blank

### Requirement: User Organization Scoping
Users SHALL belong to the organization of the creator and be inaccessible outside that organization.

#### Scenario: Organization-scoped user access
Given a user creation request
When created by a user from organization X
Then the new user belongs to organization X
And can only be accessed by users from organization X

### Requirement: User Group Assignment
Users created via HTTP endpoint SHALL be assigned to the USERS group.

#### Scenario: User group assignment
Given a user created via HTTP endpoint
Then the user is assigned to the USERS group in Keycloak
And has appropriate permissions for regular users

### Requirement: User Password Management
Newly created users SHALL receive a default password requiring reset on first access.

#### Scenario: Default password handling
Given a newly created user
Then the user receives a temporary password
And must reset password on first login
And password is managed by Keycloak

### Requirement: User Uniqueness
Usernames and emails SHALL be unique within the organization.

#### Scenario: User uniqueness validation
Given a user creation request
When username or email already exists in the organization
Then the system returns HTTP 409 Conflict
And provides clear error message

### Requirement: User Authorization
Only users with ADMIN role SHALL be able to create new users.

#### Scenario: User creation authorization
Given a user creation request
When the creator does not have ADMIN role
Then the system returns HTTP 403 Forbidden

### Requirement: Username Validation Rules
Username inputs SHALL be validated according to specified constraints.

#### Scenario: Username validation rules
Given a user creation request
Then username must be 3-50 characters and not empty or blank

### Requirement: Fullname Validation Rules
Fullname inputs SHALL be validated according to specified constraints.

#### Scenario: Fullname validation rules
Given a user creation request
Then fullname must be 3-200 characters and not empty or blank

### Requirement: Email Validation Rules
Email inputs SHALL be validated according to specified constraints.

#### Scenario: Email validation rules
Given a user creation request
Then email must be valid format, max 254 characters, and not empty or blank

### Requirement: User Audit Trail
All user creation SHALL be audited with creation metadata.

#### Scenario: User creation audit
Given a user creation event
Then the user entity includes createdBy, createdAt, updatedBy, updatedAt
And audit information is properly maintained