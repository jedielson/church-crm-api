<!-- OPENSPEC:START -->
# OpenSpec Instructions

These instructions are for AI assistants working in this project.

Always open `@/openspec/AGENTS.md` when the request:
- Mentions planning or proposals (words like proposal, spec, change, plan)
- Introduces new capabilities, breaking changes, architecture shifts, or big performance/security work
- Sounds ambiguous and you need the authoritative spec before coding

Use `@/openspec/AGENTS.md` to learn:
- How to create and apply change proposals
- Spec format and conventions
- Project structure and guidelines

Keep this managed block so 'openspec update' can refresh the instructions.

<!-- OPENSPEC:END -->

# AGENTS.md - AI Code Generation Guide

**Purpose**: This file helps AI agents generate code that follows the Church CRM architectural patterns and rules.

**Last Updated**: 2026-01-09  
**Version**: 1.0

---

## 🛠️ Serena MCP Tools (REQUIRED)

**All file operations MUST use Serena MCP tools only.**

### Essential Serena Tools
| Tool | Purpose | Example |
|------|---------|---------|
| `serena_read_file` | Read file contents | Read existing code |
| `serena_create_text_file` | Create new files | New entities, DTOs, etc. |
| `serena_replace_content` | Edit existing files | Modify templates |
| `serena_find_file` | Find files by pattern | Locate existing implementations |
| `serena_search_for_pattern` | Search code | Find patterns across codebase |
| `serena_execute_shell_command` | Run commands | Compile, test, Maven commands |
| `serena_get_symbols_overview` | Analyze file structure | Understand code layout |

### Usage Rules
- ✅ **ALWAYS** use `serena_read_file` before editing files
- ✅ **ALWAYS** use `serena_create_text_file` for new files
- ✅ **ALWAYS** use `serena_replace_content` for existing file edits
- ✅ **ALWAYS** use `serena_execute_shell_command` for Maven commands
- ❌ **NEVER** use legacy tools: Read, Write, Edit, Glob, Grep, Bash

### Example Workflow
```bash
# Find existing entity pattern
serena_find_file "*Entity.java" "src"

# Read existing entity for reference
serena_read_file "src/main/java/.../Church.java"

# Create new entity using template
serena_create_text_file "src/main/java/.../Product.java" "entity_template_content"

# Compile and test
serena_execute_shell_command "./mvnw clean compile"
serena_execute_shell_command "./mvnw test -Dtest=ArchitectureTest"
```

---

## 🤖 How to Use This File

When generating code for this project, **always**:
1. Read this file first to understand patterns
2. **Use Serena MCP tools ONLY** - all file operations must use Serena tools
3. Follow the templates provided
4. Run ArchUnit tests after generating code
5. Ensure all 8 ArchUnit rules pass

---

## 📋 Quick Reference Checklist

Before generating any code, verify:

- [ ] Entity has `@Id UUID id`
- [ ] Entity has `@Embedded Audit audit`
- [ ] Entity has `@EntityListeners(AuditingEntityListener.class)`
- [ ] Entity extends **nothing** (no inheritance)
- [ ] Controller methods return `ResponseEntity<T>`
- [ ] `@OrganizationId` parameters use `UUID` type
- [ ] DTOs are immutable `record` types
- [ ] Exceptions extend base exception types
- [ ] MapStruct mapper for entity ↔ DTO conversion
- [ ] OpenAPI annotations on endpoints

---

## 🏗️ Code Generation Templates

### Template 1: Creating a New Entity

```java
package org.churchcrm.churchcrmapi.{module}.internal;

import jakarta.persistence.*;
import lombok.*;
import org.churchcrm.churchcrmapi.crosscutting.auditing.Audit;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

/**
 * {Entity description}
 * 
 * ArchUnit Rules:
 * - Rule #1: Has @Embedded Audit field
 * - Rule #2: Uses UUID for @Id
 * - Rule #4: No inheritance (extends nothing)
 * - Rule #6: Has @EntityListeners(AuditingEntityListener.class)
 */
@Entity
@Table(schema = "{schema}", name = "{table_name}")
@EntityListeners(AuditingEntityListener.class)  // ← REQUIRED for Rule #6
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class {EntityName} {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;  // ← REQUIRED: Rule #2 (must be UUID)

    @Embedded
    private Audit audit;  // ← REQUIRED: Rule #1 (must be named "audit")

    // Domain fields below
    @Column(nullable = false)
    private String name;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ParentEntity parent;
}
```

**Key Rules**:
- ✅ No `extends` (Rule #4)
- ✅ UUID `@Id` (Rule #2)
- ✅ `@Embedded Audit audit` (Rule #1)
- ✅ `@EntityListeners(AuditingEntityListener.class)` (Rule #6)
- ✅ Audit field named exactly `audit` (Rule #5)

---

### Template 2: Creating a DTO

```java
package org.churchcrm.churchcrmapi.{module};

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {Entity}.
 * Immutable record type for API responses.
 */
public record {Entity}Dto(
    UUID id,
    String name,
    // ... domain fields
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {}

/**
 * DTO for creating {Entity}.
 * No id or audit fields (server-generated).
 * Address fields are optional - provide null for no address.
 */
public record Create{Entity}Dto(
    @NotBlank String name,
    // ... other required fields
    AddressDto address  // Optional - null allowed
) {}

/**
 * DTO for updating {Entity}.
 * Optional fields as needed.
 */
public record Update{Entity}Dto(
    String name
    // ... updatable fields only
) {}
```

**Key Rules**:
- ✅ Immutable `record` types
- ✅ Include audit fields in response DTOs
- ✅ No id/audit fields in Create DTOs
- ✅ Address fields are optional - provide null for no address
- ✅ Use @NotBlank, @Size, @Email for validation
- ✅ Package: root module package (public API)

---

### Template 3: Creating a MapStruct Mapper

```java
package org.churchcrm.churchcrmapi.{module}.internal;

import org.churchcrm.churchcrmapi.{module}.{Entity}Dto;
import org.churchcrm.churchcrmapi.{module}.Create{Entity}Dto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {Entity}.
 */
@Mapper(componentModel = "spring")
public interface {Entity}Mapper {

    /**
     * Converts entity to DTO.
     * Automatically maps audit fields (createdAt, updatedAt, etc.)
     */
    {Entity}Dto toDto({Entity} entity);

    /**
     * Converts Create DTO to entity.
     * Ignores id and audit (auto-generated).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    {Entity} toEntity(Create{Entity}Dto dto);
}
```

**Key Rules**:
- ✅ `componentModel = "spring"` for dependency injection
- ✅ Ignore `id` and `audit` in create mappings
- ✅ Package: `{module}.internal` (not public API)

---

### Template 4: Creating a Repository

```java
package org.churchcrm.churchcrmapi.{module}.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {Entity}.
 * Internal to the module.
 */
@Repository
interface {Entity}Repository extends JpaRepository<{Entity}, UUID> {

    // Query methods follow Spring Data JPA naming conventions
    Optional<{Entity}> findByName(String name);
    
    boolean existsByName(String name);
    
    List<{Entity}> findByParentId(UUID parentId);
}
```

**Key Rules**:
- ✅ Package-private (`interface` without `public`)
- ✅ Extends `JpaRepository<Entity, UUID>`
- ✅ Package: `{module}.internal`
- ✅ Use Spring Data JPA query methods

**File Creation**:
Use `serena_create_text_file` with the template content, replacing placeholders with actual names.

---

### Template 5: Creating a Service

```java
package org.churchcrm.churchcrmapi.{module}.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churchcrm.churchcrmapi.{module}.{Entity}Dto;
import org.churchcrm.churchcrmapi.{module}.Create{Entity}Dto;
import org.churchcrm.churchcrmapi.crosscutting.web.NotFoundException;
import org.churchcrm.churchcrmapi.crosscutting.web.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for {Entity} operations.
 * Internal to the module.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class {Entity}Service {

    private final {Entity}Repository repository;
    private final {Entity}Mapper mapper;

    /**
     * Get entity by id with organization scope.
     * Returns 404 if not found or unauthorized.
     */
    public {Entity}Dto getById(UUID id, UUID organizationId) {
        {Entity} entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("{Entity}", id));
        
        // Security: verify organization match
        if (!entity.getOrganizationId().equals(organizationId)) {
            throw new NotFoundException("{Entity}", id);  // 404, not 403
        }
        
        return mapper.toDto(entity);
    }

    /**
     * Create new entity.
     */
    @Transactional
    public {Entity}Dto create(Create{Entity}Dto dto, UUID organizationId) {
        // Validation - check uniqueness constraints
        if (repository.existsByName(dto.name())) {
            throw new ConflictException(
                "Entity with this name already exists",
                "name",
                dto.name()
            );
        }

        // Create entity
        {Entity} entity = mapper.toEntity(dto);
        entity.setOrganizationId(organizationId);

        entity = repository.save(entity);

        log.info("Created {Entity}: id={}, name={}", entity.getId(), entity.getName());

        return mapper.toDto(entity);
    }

    /**
     * Delete entity.
     */
    @Transactional
    public void delete(UUID id, UUID organizationId) {
        {Entity} entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("{Entity}", id));
        
        // Security check
        if (!entity.getOrganizationId().equals(organizationId)) {
            throw new NotFoundException("{Entity}", id);
        }
        
        repository.delete(entity);
        log.info("Deleted {Entity}: id={}", id);
    }
}
```

**Key Rules**:
- ✅ Package-private (no `public`)
- ✅ `@Transactional(readOnly = true)` by default
- ✅ `@Transactional` on write methods
- ✅ Always check `organizationId` for security
- ✅ Throw `NotFoundException` (404) for unauthorized, not `ForbiddenException` (403)
- ✅ Check uniqueness constraints before saving (hostname, name, etc.)
- ✅ Log important operations
- ✅ Package: `{module}.internal`

---

### Template 6: Creating a Controller

```java
package org.churchcrm.churchcrmapi.{module}.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.churchcrm.churchcrmapi.crosscutting.web.OrganizationId;
import org.churchcrm.churchcrmapi.{module}.{Entity}Dto;
import org.churchcrm.churchcrmapi.{module}.Create{Entity}Dto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for {Entity} operations.
 */
@RestController
@RequestMapping("/{endpoint-path}")
@RequiredArgsConstructor
@Tag(name = "{Entities}", description = "{Entity} management endpoints")
public class {Entity}Controller {

    private final {Entity}Service service;

    /**
     * Get {entity} by ID.
     * ArchUnit Rule #8: MUST return ResponseEntity
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get {entity} by ID",
        description = "Retrieves {entity} details. Users can only access their own organization.",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "{Entity} found",
            content = @Content(schema = @Schema(implementation = {Entity}Dto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "{Entity} not found or access denied",
            content = @Content
        )
    })
    public ResponseEntity<{Entity}Dto> get(
            @PathVariable UUID id,
            @OrganizationId UUID organizationId) {  // ← Rule #7: Must be UUID
        
        {Entity}Dto dto = service.getById(id, organizationId);
        return ResponseEntity.ok(dto);  // ← Rule #8: Return ResponseEntity
    }

    /**
     * Create new {entity}.
     * ArchUnit Rule #8: MUST return ResponseEntity
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")  // or 'ADMIN' as needed
    @Operation(
        summary = "Create {entity}",
        description = "Creates a new {entity}.",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "{Entity} created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - {entity} already exists",
            content = @Content
        )
    })
    public ResponseEntity<{Entity}Dto> create(
            @RequestBody @Valid Create{Entity}Dto dto,
            @OrganizationId UUID organizationId) {
        
        {Entity}Dto created = service.create(dto, organizationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);  // ← Rule #8
    }

    /**
     * Delete {entity}.
     * ArchUnit Rule #8: MUST return ResponseEntity
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete {entity}",
        description = "Deletes {entity}. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "{Entity} deleted successfully"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - requires ADMIN role",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "{Entity} not found",
            content = @Content
        )
    })
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @OrganizationId UUID organizationId) {
        
        service.delete(id, organizationId);
        return ResponseEntity.noContent().build();  // ← Rule #8
    }
}
```

**Key Rules**:
- ✅ All methods return `ResponseEntity<T>` (Rule #8)
- ✅ `@OrganizationId UUID organizationId` parameters (Rule #7)
- ✅ `@Valid` annotation on request bodies for validation
- ✅ OpenAPI annotations for documentation
- ✅ `@PreAuthorize` for role-based access
- ✅ Package: `{module}.internal`

---

### Template 7: Creating an Exception

```java
package org.churchcrm.churchcrmapi.crosscutting.web;

import java.util.UUID;

/**
 * Exception for {Entity} not found.
 * Extends NotFoundException for automatic 404 handling.
 */
public class {Entity}NotFoundException extends NotFoundException {
    
    public {Entity}NotFoundException(UUID id) {
        super("{Entity}", id);
    }
    
    public {Entity}NotFoundException(String message) {
        super(message);
    }
}
```

**Available Base Exceptions**:
- `ValidationException` → 400 Bad Request
- `NotFoundException` → 404 Not Found
- `ConflictException` → 409 Conflict
- `ForbiddenException` → 403 Forbidden
- `UnauthorizedException` → 401 Unauthorized

**Key Rules**:
- ✅ Extend appropriate base exception
- ✅ Package: `org.churchcrm.churchcrmapi.crosscutting.web`
- ✅ GlobalExceptionHandler automatically handles all base types

---

### Template 8: Creating a Domain Event

```java
package org.churchcrm.churchcrmapi.{module};

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when {entity} is {action}.
 * Public API - can be consumed by other modules.
 */
public record {Entity}{Action}(
    UUID entityId,
    String entityName,
    Instant occurredAt
) {
    public {Entity}{Action}(UUID entityId, String entityName) {
        this(entityId, entityName, Instant.now());
    }
}
```

**Publishing Events**:
```java
@Service
class {Entity}Service {
    private final ApplicationEventPublisher events;
    private final {Entity}Mapper mapper;

    @Transactional
    public void create(Create{Entity}Dto dto) {
        {Entity} entity = repository.save(entity);

        // Publish event using MapStruct mapper
        var event = mapper.to{Entity}Created(entity, dto);
        events.publishEvent(event);
    }
}
```

**Listening to Events**:
```java
@Service
class SomeOtherService {
    
    @ApplicationModuleListener  // Spring Modulith listener
    public void on({Entity}Created event) {
        // React to event
        log.info("Handling {Entity}Created: {}", event.entityId());
    }
}
```

**Key Rules**:
- ✅ Immutable `record` types
- ✅ Package: root module package (public API)
- ✅ Include timestamp
- ✅ Use `@ApplicationModuleListener` for cross-module events

---

## 🔒 Security Patterns

### Pattern 1: Organization-Scoped Access

**Always** validate organization ownership:

```java
public {Entity}Dto getById(UUID id, UUID organizationId) {
    {Entity} entity = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("{Entity}", id));
    
    // ⚠️ SECURITY CHECK - REQUIRED
    if (!entity.getOrganizationId().equals(organizationId)) {
        throw new NotFoundException("{Entity}", id);  // 404, not 403!
    }
    
    return mapper.toDto(entity);
}
```

### Pattern 2: Security Through Obscurity

**Return 404 (not 403) for unauthorized access**:

```java
// ✅ CORRECT - Hides existence
throw new NotFoundException("{Entity}", id);  // 404

// ❌ WRONG - Reveals existence
throw new ForbiddenException("Access denied");  // 403
```

### Pattern 3: Role-Based Access Control

```java
@PreAuthorize("hasRole('ADMIN')")  // Only admins
public ResponseEntity<Void> delete(...) { }

@PreAuthorize("hasRole('USER')")  // All authenticated users
public ResponseEntity<Dto> create(...) { }

// No annotation = requires authentication, no specific role
public ResponseEntity<Dto> get(...) { }
```

---

## 📦 Package Structure Rules

### Module Package Layout

```
{module}/
├── {Entity}.java                    # Entity (internal)
├── {Entity}Dto.java                 # DTO (public API)
├── Create{Entity}Dto.java           # Create DTO (public API)
├── {Entity}Created.java             # Event (public API)
└── internal/
    ├── {Entity}Repository.java      # Repository (internal)
    ├── {Entity}Service.java         # Service (internal)
    ├── {Entity}Controller.java      # Controller (internal)
    └── {Entity}Mapper.java          # Mapper (internal)
```

**Public API** (root package):
- DTOs
- Events
- Service interfaces (if needed)

**Internal** (internal package):
- Repositories
- Service implementations
- Controllers
- Mappers

---

## ✅ ArchUnit Rules Reference

### Rule #1: Embedded Audit Field
```java
@Embedded
private Audit audit;  // Must be named "audit"
```

### Rule #2: UUID Primary Keys
```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;  // Must be UUID type
```

### Rule #3: Audit Class @Embeddable
```java
@Embeddable
public record Audit(...) {}
```

### Rule #4: No Entity Inheritance
```java
// ❌ NOT ALLOWED
@Entity
class Child extends Parent { }

// ✅ CORRECT
@Entity
class Child {
    @Embedded private Audit audit;
}
```

### Rule #5: Audit Field Naming
```java
@Embedded
private Audit audit;  // Must be named exactly "audit"
```

### Rule #6: EntityListeners Annotation
```java
@Entity
@EntityListeners(AuditingEntityListener.class)  // REQUIRED
public class {Entity} { }
```

### Rule #7: @OrganizationId Must Use UUID
```java
// ✅ CORRECT
public ResponseEntity<Dto> get(@OrganizationId UUID organizationId) { }

// ❌ WRONG - Fails ArchUnit test
public ResponseEntity<Dto> get(@OrganizationId String organizationId) { }
```

### Rule #8: Controllers Return ResponseEntity
```java
// ✅ CORRECT
@GetMapping("/{id}")
public ResponseEntity<Dto> get(@PathVariable UUID id) {
    return ResponseEntity.ok(service.get(id));
}

// ❌ WRONG - Fails ArchUnit test
@GetMapping("/{id}")
public Dto get(@PathVariable UUID id) {
    return service.get(id);
}
```

---

## 🧪 Testing Generated Code

After generating code, **always run**:

```bash
# Compile
./mvnw clean compile

# Run ArchUnit tests
./mvnw test -Dtest=ArchitectureTest

# Check specific rule
./mvnw test -Dtest=ArchitectureTest#organizationIdParametersShouldBeUuid
```

**All 8 tests must pass** before considering code complete.

---

## 🚨 Common Mistakes to Avoid

### ❌ Mistake 1: Returning DTO directly from controller
```java
// WRONG
@GetMapping("/{id}")
public ChurchDto get(@PathVariable UUID id) {
    return service.get(id);  // Fails Rule #8
}

// CORRECT
@GetMapping("/{id}")
public ResponseEntity<ChurchDto> get(@PathVariable UUID id) {
    return ResponseEntity.ok(service.get(id));
}
```

### ❌ Mistake 2: Using String for @OrganizationId
```java
// WRONG
public void method(@OrganizationId String org) { }  // Fails Rule #7

// CORRECT
public void method(@OrganizationId UUID org) { }
```

### ❌ Mistake 3: Missing Audit field
```java
// WRONG
@Entity
public class Product {
    @Id private UUID id;
    // Missing @Embedded Audit audit;  // Fails Rule #1
}

// CORRECT
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id private UUID id;
    @Embedded private Audit audit;
}
```

### ❌ Mistake 4: Using entity inheritance
```java
// WRONG - Fails Rule #4
@Entity
abstract class BaseEntity { }

@Entity
class Product extends BaseEntity { }

// CORRECT - Use composition
@Entity
class Product {
    @Embedded private Audit audit;
}
```

### ❌ Mistake 5: Not checking organization ownership
```java
// WRONG - Security vulnerability!
public Dto get(UUID id, UUID organizationId) {
    return repository.findById(id)
        .map(mapper::toDto)
        .orElseThrow(() -> new NotFoundException("Entity", id));
}

// CORRECT - Check ownership
public Dto get(UUID id, UUID organizationId) {
    Entity entity = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("Entity", id));
    
    if (!entity.getOrganizationId().equals(organizationId)) {
        throw new NotFoundException("Entity", id);  // 404!
    }
    
    return mapper.toDto(entity);
}
```

### ❌ Mistake 6: Returning 403 for unauthorized access
```java
// WRONG - Leaks information
if (!entity.getOrganizationId().equals(organizationId)) {
    throw new ForbiddenException("Access denied");  // 403 reveals it exists
}

// CORRECT - Security through obscurity
if (!entity.getOrganizationId().equals(organizationId)) {
    throw new NotFoundException("Entity", id);  // 404 hides existence
}
```

---

## 📚 Additional Resources

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Full architectural decisions document
- **[EXCEPTION_HANDLING.md](EXCEPTION_HANDLING.md)** - Exception handling patterns
- **[ARCHUNIT_ORGANIZATION_ID_RULE.md](ARCHUNIT_ORGANIZATION_ID_RULE.md)** - @OrganizationId rule details
- **[ARCHUNIT_CONTROLLER_RESPONSE_RULE.md](ARCHUNIT_CONTROLLER_RESPONSE_RULE.md)** - ResponseEntity rule details

---

## 🗄️ Database Constraints for Business Rules

When implementing business rules that must be enforced at the database level:

- **Uniqueness constraints**: Add UNIQUE indexes for fields like hostname
- **Partial indexes**: Use WHERE clauses for conditional uniqueness (e.g., one main congregation per church)
- **Foreign keys**: Always include CASCADE DELETE for proper relationship management
- **Check constraints**: For complex business rules that can be expressed in SQL

Example partial unique index for one main congregation per church:
```sql
CREATE UNIQUE INDEX idx_congregations_main_per_church
ON organization.congregations(church_id)
WHERE is_main = true;
```

---

## 🔄 Code Generation Workflow

1. **Read AGENTS.md** (this file)
2. **Choose appropriate template** from above
3. **Replace placeholders** with actual names
4. **Generate code** using Serena MCP tools only:
   - `serena_create_text_file` for new files
   - `serena_replace_content` for existing file edits
   - `serena_read_file` to read files
   - `serena_find_file` to find files
   - `serena_search_for_pattern` to search code
   - `serena_execute_shell_command` for compilation/testing
5. **Run compilation**: `serena_execute_shell_command "./mvnw clean compile"`
6. **Run ArchUnit tests**: `serena_execute_shell_command "./mvnw test -Dtest=ArchitectureTest"`
7. **Fix violations** if any using Serena tools
8. **Verify all 8 rules pass**
9. **Run full test suite**: `serena_execute_shell_command "./mvnw test"`
10. **Code review** and merge

---

## 🎯 Success Criteria

Generated code is complete when:

✅ Compiles without errors  
✅ All 8 ArchUnit rules pass  
✅ Follows templates from this document  
✅ Has OpenAPI documentation  
✅ Includes organization-scoped security  
✅ Returns ResponseEntity from controllers  
✅ Uses base exception types  
✅ Has proper logging  

---

## 📝 Template Variables Reference

Common placeholders used in templates:

| Placeholder | Example | Description |
|-------------|---------|-------------|
| `{module}` | `organization` | Module name |
| `{Entity}` | `Church` | Entity class name (PascalCase) |
| `{entity}` | `church` | Entity name (lowercase) |
| `{Entities}` | `Churches` | Plural entity name |
| `{endpoint-path}` | `churches` | URL path (plural, kebab-case) |
| `{schema}` | `organization` | Database schema name |
| `{table_name}` | `churches` | Database table name (snake_case) |
| `{Action}` | `Created` | Event action (PascalCase) |
| `{action}` | `created` | Event action (lowercase) |

---

## 🆘 Troubleshooting

### Problem: ArchUnit test fails

**Solution**: Check which rule failed and consult the corresponding template above.

### Problem: Compilation error on @OrganizationId

**Solution**: Ensure parameter type is `UUID`, not `String` or any other type (Rule #7).

### Problem: MapStruct not generating implementation

**Solution**: Run `serena_execute_shell_command "./mvnw clean compile"` to trigger annotation processing.

### Problem: Audit fields not populated

**Solution**: Ensure entity has `@EntityListeners(AuditingEntityListener.class)` (Rule #6).

### Problem: Controller method returns wrong status

**Solution**: Use explicit `ResponseEntity.status()` or helper methods like `ResponseEntity.ok()`.

### Problem: Using legacy file tools

**Solution**: Always use Serena MCP tools instead of legacy tools:
- Use `serena_read_file` instead of `read`
- Use `serena_create_text_file` instead of `write`
- Use `serena_replace_content` instead of `edit`
- Use `serena_search_for_pattern` instead of `grep` or `rg`
- Use `serena_find_file` instead of `glob`
- Use `serena_execute_shell_command` instead of `bash`

---

**Remember**: When in doubt, check existing code in the `organization` module for reference implementations!
