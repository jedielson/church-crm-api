# Tasks for Add User Management Capability

## Identity Management Module Setup
- [ ] Create identity module package structure (`identity/` and `identity.internal/`)
- [ ] Define User entity for Users table with UUID id, Audit embeddable, Keycloak user ID, and organization ID (identity schema)
- [ ] Create UserDto and CreateUserDto records
- [ ] Implement UserRepository with organization-scoped queries
- [ ] Create UserMapper for entity-DTO conversion

## User Creation Service
- [ ] Implement UserService with create method (organization-scoped)
- [ ] Add Keycloak user creation logic with group assignment (USERS)
- [ ] Add validation for uniqueness and organization access
- [ ] Publish UserCreated event via Spring Modulith

## User Creation Controller
- [ ] Create UserController with POST endpoint for user creation
- [ ] Add @OrganizationId parameter for scoping
- [ ] Implement proper OpenAPI documentation
- [ ] Return ResponseEntity<UserDto> with HTTP 201

## Organization Admin User Creation
- [ ] Modify ChurchService to create admin user in ADMIN group during organization creation
- [ ] Update ChurchCreated event to include admin user details
- [ ] Ensure admin user gets temporary password requiring reset

## Security and Validation
- [ ] Add @PreAuthorize for user creation (ADMIN role required)
- [ ] Implement organization ownership validation
- [ ] Add comprehensive input validation on DTOs
- [ ] Return 404 for unauthorized organization access

## Testing
- [ ] Write unit tests for UserService and UserController
- [ ] Add integration tests for user creation endpoint
- [ ] Update ChurchCreationIntegrationTest for admin user creation
- [ ] Verify ArchUnit tests pass for new code

## Documentation and Validation
- [ ] Update OpenAPI specs for new endpoints
- [ ] Validate all spec deltas are correctly defined
- [ ] Run openspec validate to ensure compliance
- [ ] Test cross-module integration with organization module