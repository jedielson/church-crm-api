# Church Creation Design

## Architecture Overview
The church creation feature follows the established Spring Modulith architecture with organization-scoped security. The flow involves:

1. REST Controller receives CreateChurchDto
2. Service layer validates input and creates Church entity
3. Domain factory method creates Church with main Congregation
4. Repository persists the entities
5. ChurchCreated event triggers Keycloak user creation

## Validation Strategy
Validation will be implemented at multiple levels:
- **DTO Level**: Bean validation annotations for basic constraints
- **Domain Level**: Business rule validation in Church.create factory method
- **Service Level**: Conflict checks (e.g., hostname uniqueness)

## Key Design Decisions
- **Address Optionality**: Address is optional, contrary to current implementation
- **Validation Location**: Domain validation in factory method to ensure consistency
- **Error Handling**: Use existing base exceptions (ValidationException, ConflictException)
- **Congregation Creation**: Automatic main congregation with church name inheritance

## Trade-offs
- Moving from required to optional address changes API contract (breaking change)
- Adding validation increases complexity but improves data quality
- Domain-level validation ensures business rules are enforced regardless of entry point