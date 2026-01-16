package org.churchcrm.churchcrmapi.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a new church with admin user")
public record CreateChurchDto(
        @Schema(description = "Church name", example = "First Baptist Church", required = true)
        @NotBlank(message = "Church name cannot be blank")
        @Size(min = 3, max = 200, message = "Church name must be between 3 and 200 characters")
        String name,

        @Schema(description = "Unique hostname for the church", example = "first-baptist.church", required = true)
        @NotBlank(message = "Hostname cannot be blank")
        @Size(min = 3, max = 50, message = "Hostname must be between 3 and 50 characters")
        String hostName,

        @Schema(description = "Username for the church admin", example = "john.doe", required = true)
        @NotBlank(message = "Username cannot be blank")
        String userName,

        @Schema(description = "Email address for the church admin (will be used as Keycloak username)",
                example = "john.doe@first-baptist.church", required = true)
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be a valid email address")
        String email,

        @Schema(description = "Full name of the church admin user", example = "John Doe", required = true)
        @NotBlank(message = "Full name cannot be blank")
        String fullName,

        @Schema(description = "Physical address of the main church location", required = false)
        @Valid
        AddressDto address
) {
}
