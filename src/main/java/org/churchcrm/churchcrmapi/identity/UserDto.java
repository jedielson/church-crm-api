package org.churchcrm.churchcrmapi.identity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for User.
 * Immutable record type for API responses.
 */
@Schema(description = "User information")
public record UserDto(
    @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID id,
    
    @Schema(description = "Username", example = "john.doe")
    String username,
    
    @Schema(description = "Full name", example = "John Doe")
    String fullname,
    
    @Schema(description = "Email address", example = "john.doe@example.com")
    String email,
    
    @Schema(description = "Church ID (organization)", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID churchId,
    
    @Schema(description = "User who created this user")
    String createdBy,
    
    @Schema(description = "Creation timestamp")
    Instant createdAt,
    
    @Schema(description = "User who last modified this user")
    String updatedBy,
    
    @Schema(description = "Last modification timestamp")
    Instant updatedAt
) {}