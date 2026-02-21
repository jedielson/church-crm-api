package org.churchcrm.churchcrmapi.identity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

/**
 * DTO for creating User.
 * No id or audit fields (server-generated).
 */
@Schema(description = "Request to create a new user")
@Value
@AllArgsConstructor
public class CreateUserDto{
    @Schema(description = "Username", example = "john.doe", required = true)
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Full name", example = "John Doe", required = true)
    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 3, max = 200, message = "Full name must be between 3 and 200 characters")
    private String fullname;

    @Schema(description = "Email address", example = "john.doe@example.com", required = true)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 254, message = "Email must be at most 254 characters")
    private String email;
}