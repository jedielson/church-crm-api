# Add Identity Management Capability

## Summary
This change introduces identity management functionality to the Church CRM API, allowing system maintainers to create new users via HTTP endpoints.
The implementation ensures users are organization-scoped, with proper group assignments and password management.

## Key Changes
1. **New User Creation Endpoint**: HTTP POST endpoint to create users with username, fullname, email fields. Users belong to the creator's organization and are assigned to the USERS group.
2. **Automatic Admin User Creation**: Modify organization creation to automatically create an admin user in the ADMIN group.
3. **Password Management**: All created users get a hardcoded default password "password" requiring reset on first access.
4. **Users Table**: Store Keycloak user ID and organization ID in a dedicated Users table (identity schema) for organization scoping.
5. **Keycloak Integration**: Users are created in Keycloak with appropriate group memberships.

## Assumptions
- User creation via endpoint requires ADMIN role for the creator.
- Default password is handled by Keycloak's temporary password mechanism.
- Username and email uniqueness is enforced at Keycloak level.
- Organization-scoped access ensures users can only manage users within their organization.

## Impact
- Adds new identity module for user management capabilities.
- Maintains existing security model with organization scoping.
- Requires Keycloak integration for user lifecycle management.

## Validation
- All changes must pass ArchUnit tests.
- Integration tests for user creation endpoints.
- Cross-module event handling for user-related events.

## Related Specs
- `organization/spec.md`: Modified to specify admin user creation
- `identity/spec.md`: New specification for user management capabilities