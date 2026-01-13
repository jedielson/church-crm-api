# Organization Management

## ADDED Requirements

### Requirement: Church Creation Capability
The system SHALL allow creation of new church organizations with automatic main congregation setup.

#### Scenario: Church creation capability
Given the church creation feature
When implemented
Then churches can be created with congregations

### Requirement: Church Creation Response
The system SHALL return the created church data and Location header upon successful creation.

#### Scenario: Church creation response
Given a valid church creation request
When the church is successfully created
Then the system returns HTTP 201 with the created ChurchDto in the response body
And the Location header points to the getById route for the created church

### Requirement: Church Name Validation
Church names SHALL meet length and content requirements.

#### Scenario: Church name validation
Given church name input
Then it must be validated for length and content

### Requirement: Hostname Validation
Hostnames SHALL be unique and meet length requirements.

#### Scenario: Hostname validation
Given hostname input
Then it must be validated for uniqueness and length

### Requirement: User Information Validation
User name, email, and full name SHALL be valid.

#### Scenario: User information validation
Given user name, email, and full name input
Then they must be validated

### Requirement: Full Name Validation Rules
Full name inputs SHALL be validated according to specified constraints.

#### Scenario: Full name validation rules
Given a church creation request
Then full name must not be empty or blank

### Requirement: Address Validation
Address fields SHALL be validated when provided.

#### Scenario: Address validation
Given address input when provided
Then fields must be validated

### Requirement: Name Validation Rules
Name inputs SHALL be validated according to specified constraints.

#### Scenario: Name validation rules
Given a church creation request
Then name must be 3-200 characters and not empty or blank

### Requirement: Hostname Validation Rules
Hostname inputs SHALL be validated according to specified constraints.

#### Scenario: Hostname validation rules
Given a church creation request
Then hostname must be 3-50 characters and not empty or blank

### Requirement: Username Validation Rules
Username inputs SHALL be validated according to specified constraints.

#### Scenario: Username validation rules
Given a church creation request
Then username must not be empty or blank

### Requirement: Email Validation Rules
Email inputs SHALL be validated according to specified constraints.

#### Scenario: Email validation rules
Given a church creation request
Then email must be valid format and not empty or blank

### Requirement: Address Validation Rules
Address inputs SHALL be validated according to specified constraints when provided.

#### Scenario: Address validation rules
Given a church creation request with address
Then address line 1 must be 3-200 characters
And city must be 3-100 characters
And address line 2 must be 3-200 characters if provided
And postal code must be 3-20 characters if provided