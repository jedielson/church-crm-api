# Design for Add Identity Management Capability

## Architectural Overview
The user management feature extends the existing organization-scoped architecture to include user lifecycle management. 
Users are created in Keycloak and mirrored in the application database for organizational relationships and audit trails.

## Key Design Decisions

### User Entity Model
- **Users Table**: Stores Keycloak user ID and organization ID for organization scoping (identity schema)
- **UUID Primary Keys**: Consistent with existing entities
- **Embedded Audit**: Tracks creation/modification metadata
- **No Inheritance**: Follows Rule #4, composition over inheritance
- **Keycloak User ID**: Reference to Keycloak user for authentication linkage
- **Schema Segregation**: Users table in identity schema, separate from organization schema

### Group Management
- **ADMIN Group**: Assigned to users created during organization setup
- **USERS Group**: Assigned to users created via HTTP endpoint
- **Keycloak Groups**: Mapped to application roles for authorization

### Password Management
- **Default Password**: Hardcoded "password" for all new users
- **Required Reset**: Enforced on first login via Keycloak configuration
- **Security**: Passwords never stored in application database

### Security Model
- **Organization Scoping**: All user operations validate organization ownership
- **Role-Based Access**: User creation requires ADMIN role
- **Security Through Obscurity**: Return 404 for unauthorized access attempts
- **JWT Claims**: @OrganizationId parameter for automatic scoping

### Keycloak Integration
- **User Creation**: REST API calls to Keycloak Admin API
- **Group Assignment**: Automatic group membership during creation
- **Event Handling**: Listen for Keycloak user events if needed
- **Fallback**: Graceful degradation if Keycloak is unavailable

### Event-Driven Architecture
- **UserCreated Event**: Published for cross-module integration
- **Spring Modulith**: Ensures loose coupling between modules
- **MapStruct Mapping**: Consistent event payload structure

### Validation Strategy
- **DTO Validation**: Jakarta validation annotations on CreateUserDto
- **Service Validation**: Business rule checks (uniqueness, organization access)
- **Database Constraints**: Partial indexes for organization-scoped uniqueness

### Testing Strategy
- **Unit Tests**: Isolated testing of services and controllers
- **Integration Tests**: Full HTTP API testing with real database
- **ArchUnit Tests**: Ensure compliance with 8 architectural rules
- **Modulith Tests**: Cross-module event handling verification

## Trade-offs Considered

### Centralized vs Distributed User Management
- **Centralized (Chosen)**: Keycloak as single source of truth for authentication
- **Distributed**: Application manages users independently
- **Rationale**: Keycloak provides robust security features and simplifies integration

### Immediate vs Deferred Group Assignment
- **Immediate (Chosen)**: Groups assigned during user creation
- **Deferred**: Separate API calls for group management
- **Rationale**: Simplifies user creation workflow and reduces API calls

### Database Mirroring vs Keycloak-Only
- **Mirroring (Chosen)**: Store user data in application database
- **Keycloak-Only**: Rely solely on Keycloak for user data
- **Rationale**: Enables organization relationships, audit trails, and complex queries

## Dependencies
- **Keycloak Admin Client**: For user and group management
- **Spring Security**: For authorization checks
- **PostgreSQL**: For user entity storage
- **MapStruct**: For DTO/entity mapping
- **Spring Modulith**: For event publishing