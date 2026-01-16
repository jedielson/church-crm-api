package org.churchcrm.churchcrmapi.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Physical address information")
public record AddressDto(
        @Schema(description = "First line of the address (street address)", example = "123 Main Street", required = true)
        @NotBlank(message = "Address line 1 cannot be blank")
        @Size(min = 3, max = 200, message = "Address line 1 must be between 3 and 200 characters")
        String line1,

        @Schema(description = "Second line of the address (suite, apartment, etc.)", example = "Suite 100", required = false)
        @Size(min = 3, max = 200, message = "Address line 2 must be between 3 and 200 characters if provided")
        String line2,

        @Schema(description = "City name", example = "Springfield", required = true)
        @NotBlank(message = "City cannot be blank")
        @Size(min = 3, max = 100, message = "City must be between 3 and 100 characters")
        String city,

        @Schema(description = "Postal/ZIP code", example = "62701", required = false)
        @Size(min = 3, max = 20, message = "Postal code must be between 3 and 20 characters if provided")
        String postalCode
) {
}
