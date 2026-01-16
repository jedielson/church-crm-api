package org.churchcrm.churchcrmapi.organization.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.churchcrm.churchcrmapi.crosscutting.web.OrganizationId;
import org.churchcrm.churchcrmapi.organization.ChurchDto;
import org.churchcrm.churchcrmapi.organization.CreateChurchDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/churches")
@AllArgsConstructor
@Tag(name = "Churches", description = "Church management endpoints")
public class OrganizationController {

    private final ChurchService churchService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new church",
            description = "Creates a new church in the system and automatically creates a Keycloak user for the church admin. " +
                    "The user will be added to the USERS group with a temporary password that must be changed on first login.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Church created successfully. A Keycloak user has been created with the provided email."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN role",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Church with this hostname already exists",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid church data",
                    content = @Content
            )
    })
    public ResponseEntity<ChurchDto> createChurch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Church details including name, hostname, admin user email, and address",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateChurchDto.class))
            )
            @RequestBody @Valid CreateChurchDto church) {

        ChurchDto created = churchService.createChurch(church);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get church by ID",
            description = "Retrieves church details by ID. Users can only access their own church based on the " +
                    "organization_id JWT claim. Returns 404 if the church doesn't exist or if the user doesn't " +
                    "have access (to prevent information leakage).",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Church found and user has access",
                    content = @Content(schema = @Schema(implementation = ChurchDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Church not found or user does not have access to this church",
                    content = @Content
            )
    })
    public ResponseEntity<ChurchDto> getChurch(
            @Parameter(description = "Church UUID", required = true, example = "9987eb71-8050-4e44-8f4e-179564bfccca")
            @PathVariable UUID id,
            @OrganizationId UUID organizationId) {
        ChurchDto church = churchService.getChurchById(id, organizationId);
        return ResponseEntity.ok(church);
    }
}
