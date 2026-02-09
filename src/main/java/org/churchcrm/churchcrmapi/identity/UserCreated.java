package org.churchcrm.churchcrmapi.identity;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when user is created.
 * Public API - can be consumed by other modules.
 * 
 * Primary key is Keycloak user ID (UUID), not generated database ID.
 */
public record UserCreated(
    UUID id,
    String entityName,
    String username,
    String fullname,
    String email,
    UUID churchId,
    Instant occurredAt
) {
    public UserCreated(UUID id, String entityName, String username, String fullname, String email, UUID churchId) {
        this(id, entityName, username, fullname, email, churchId, Instant.now());
    }
}