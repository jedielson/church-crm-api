package org.churchcrm.churchcrmapi.organization;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Physical address information")
public record AddressDto(
        @Schema(description = "First line of the address (street address)", example = "123 Main Street", required = true)
        String line1,
        
        @Schema(description = "Second line of the address (suite, apartment, etc.)", example = "Suite 100")
        String line2,
        
        @Schema(description = "City name", example = "Springfield", required = true)
        String city,
        
        @Schema(description = "Postal/ZIP code", example = "62701", required = true)
        String postalCode
) {
}
