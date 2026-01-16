# Project Context

## Purpose
Church CRM API is a Spring Boot-based REST API for managing church organizations, members, and related data. The system provides multi-tenant functionality where each church organization has isolated data access through organization-scoped security. The API supports CRUD operations for churches, members, and other church-related entities while maintaining strict security boundaries.

## Tech Stack
- **Language**: Java 21
- **Framework**: Spring Boot 4.0.1
- **Modularity**: Spring Modulith 2.0.1
- **Database**: PostgreSQL with JPA/Hibernate
- **Migrations**: Flyway
- **Mapping**: MapStruct 1.6.3
- **Code Generation**: Lombok 1.18.42
- **Security**: Spring Security with OAuth2 Resource Server
- **Authentication**: Keycloak 26.0.7
- **Documentation**: SpringDoc OpenAPI 2.7.0
- **Testing**: ArchUnit 1.3.0, Spring Boot Test
- **Build Tool**: Maven

## Project Conventions

### Code Style
- Use Lombok annotations (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @ToString)
- Follow Java naming conventions (PascalCase for classes, camelCase for methods/variables)
- Package structure: `{module}/` for public API, `{module}.internal/` for implementation
- Immutable DTOs using `record` types
- Package-private services and repositories (no `public` modifier)
- Address fields are optional in creation DTOs (provide null for no address)

### Architecture Patterns
- **Modular Architecture**: Spring Modulith for bounded contexts
- **Hexagonal Architecture**: Clean separation between domain, application, and infrastructure layers
- **Entity Patterns**:
  - UUID primary keys (generated)
  - @Embedded Audit fields for created/updated timestamps and users
  - No entity inheritance (composition over inheritance)
  - @EntityListeners(AuditingEntityListener.class)
  - Business rules enforced via domain logic and database constraints
- **DTO Patterns**: Immutable records for API contracts with validation annotations
- **Security**: Organization-scoped access with @OrganizationId parameters
- **Exception Handling**: Custom base exceptions (NotFoundException, ConflictException, etc.)
- **Mapping**: MapStruct for entity ↔ DTO conversion including event mapping
- **Domain Events**: Published via Spring Modulith with MapStruct mapping
- **Validation**: DTO-level validation with @Valid controllers, service-level uniqueness checks

### Testing Strategy
- **Architecture Tests**: ArchUnit rules enforcing 8 architectural constraints
  - Rule 1: Embedded Audit field
  - Rule 2: UUID primary keys
  - Rule 3: Audit @Embeddable
  - Rule 4: No entity inheritance
  - Rule 5: Audit field naming ("audit")
  - Rule 6: EntityListeners annotation
  - Rule 7: @OrganizationId uses UUID
  - Rule 8: Controllers return ResponseEntity
- **Unit Tests**: Domain entities, services, and controllers with mocked dependencies
- **Integration Tests**: Full HTTP API testing with MockMvc against real database (security disabled via environment variables)
- **Modulith Tests**: Cross-module integration verification
- **Validation Testing**: Comprehensive testing of input validation and error responses

### Git Workflow
- Branching: Feature branches from main, PR reviews required
- Commits: Descriptive messages following conventional commits
- Code Generation: All new code must follow AGENTS.md templates
- Pre-commit: Run `./mvnw clean compile` and `./mvnw test -Dtest=ArchitectureTest` before commits

## Domain Context
- **Church Management**: System for managing church organizations, congregations, and administrative data
- **Multi-tenancy**: Each church operates in isolation with organization-scoped data access
- **CRM Features**: Member management, event tracking, donation processing (future)
- **Security Model**: Users belong to organizations; access controlled by organization ID
- **Audit Trail**: All changes tracked with created/updated by and timestamps

## Important Constraints
- **Security First**: Return 404 (NotFoundException) instead of 403 (ForbiddenException) for unauthorized access (security through obscurity)
- **Organization Scoping**: All data operations must verify organization ownership
- **No Secrets**: Never log or expose sensitive data
- **Immutable DTOs**: All API response DTOs must be immutable records
- **ArchUnit Compliance**: All code must pass 8 ArchUnit rules
- **Business Rules**: Enforce domain constraints at both application and database levels
- **Input Validation**: Comprehensive validation with user-friendly error messages

## External Dependencies
- **PostgreSQL Database**: Primary data store with business rule constraints
- **Keycloak**: Identity and access management with event-driven user creation
- **Flyway**: Database schema migrations with partial indexes for business rules
- **Spring Modulith Runtime**: Module event handling and cross-module communication
