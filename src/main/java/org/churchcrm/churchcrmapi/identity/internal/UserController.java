package org.churchcrm.churchcrmapi.identity.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.churchcrm.churchcrmapi.crosscutting.web.OrganizationId;
import org.churchcrm.churchcrmapi.identity.CreateUserDto;
import org.churchcrm.churchcrmapi.identity.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller for User operations.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final IUserService userService;

    /**
     * Create new user.
     * ArchUnit Rule #8: MUST return ResponseEntity
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create user",
        description = "Creates a new user in the organization. Users are created with default password 'password' and assigned to USERS group.",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - requires ADMIN role",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - user already exists",
            content = @Content
        )
    })
    public ResponseEntity<UserDto> create(
            @RequestBody @Valid CreateUserDto dto,
            @OrganizationId UUID organizationId) {
        
        UserDto created = userService.createUser(dto, organizationId, false);
//        var location = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(created.id())
//                .toUri();
        return ResponseEntity.created(URI.create("/users/" + created.id())).body(created);
    }

    /**
     * Get user by ID.
     * ArchUnit Rule #8: MUST return ResponseEntity
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves user details. Users can only access their own organization.",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found or access denied",
            content = @Content
        )
    })
    public ResponseEntity<UserDto> get(
            @PathVariable UUID id,
            @OrganizationId UUID organizationId) {
        
        UserDto dto = userService.getById(id, organizationId);
        return ResponseEntity.ok(dto);
    }
}