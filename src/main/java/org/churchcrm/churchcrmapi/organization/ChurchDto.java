package org.churchcrm.churchcrmapi.organization;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Church information")
public record ChurchDto(
        @Schema(description = "Church unique identifier", example = "9987eb71-8050-4e44-8f4e-179564bfccca")
        UUID id,
        
        @Schema(description = "Church name", example = "First Baptist Church")
        String name,
        
        @Schema(description = "Church hostname/domain", example = "first-baptist.church")
        String hostName,
        
        @Schema(description = "Main congregation address")
        AddressDto mainAddress,
        
        @Schema(description = "User who created the church")
        String createdBy,
        
        @Schema(description = "Timestamp when the church was created")
        Instant createdAt,
        
        @Schema(description = "User who last modified the church")
        String updatedBy,
        
        @Schema(description = "Timestamp when the church was last modified")
        Instant updatedAt
) {
}
