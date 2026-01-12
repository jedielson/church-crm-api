package org.churchcrm.churchcrmapi.organization;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to create a new church with admin user")
public record CreateChurchDto(
        @Schema(description = "Church name", example = "First Baptist Church", required = true)
        String name,
        
        @Schema(description = "Unique hostname for the church", example = "first-baptist.church", required = true)
        String hostName,
        
        @Schema(description = "Username for the church admin", example = "john.doe", required = true)
        String userName,
        
        @Schema(description = "Email address for the church admin (will be used as Keycloak username)", 
                example = "john.doe@first-baptist.church", required = true)
        String email,
        
        @Schema(description = "Physical address of the main church location", required = true)
        AddressDto address
) {
}
