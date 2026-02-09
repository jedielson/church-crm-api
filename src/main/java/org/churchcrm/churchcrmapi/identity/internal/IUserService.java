package org.churchcrm.churchcrmapi.identity.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churchcrm.churchcrmapi.identity.CreateUserDto;
import org.churchcrm.churchcrmapi.identity.UserDto;
import org.churchcrm.churchcrmapi.identity.internal.config.KeycloakProperties;
import org.churchcrm.churchcrmapi.organization.ChurchCreated;
import org.churchcrm.churchcrmapi.crosscutting.web.NotFoundException;
import org.churchcrm.churchcrmapi.crosscutting.web.ConflictException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface IUserService {
    void createUser(ChurchCreated event);
    UserDto createUser(CreateUserDto dto, UUID organizationId, boolean firstUser);
    UserDto getById(UUID id, UUID organizationId);
    UserDto getByEmail(String email);
}

@Service
@RequiredArgsConstructor
@Slf4j
class UserService implements IUserService {

    private final Keycloak keycloak;
    private final KeycloakProperties properties;
    private final UserRepository repository;
    private final UserMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createUser(ChurchCreated event) {
        var userDto = this.mapper.toDto(event);
        var created = this.createUser(userDto, event.getChurchId(), true);
        
        // Publish UserCreated event
        var userEvent = mapper.toUserCreated(created);
        eventPublisher.publishEvent(userEvent);
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserDto dto, UUID organizationId, boolean firstUser) {
        // Validation - check uniqueness constraints
        if (repository.existsByUsernameAndChurchId(dto.getUsername(), organizationId)) {
            throw new ConflictException(
                "User with this username already exists in this organization",
                "username",
                dto.getUsername()
            );
        }

        if (repository.existsByEmailAndChurchId(dto.getEmail(), organizationId)) {
            throw new ConflictException(
                "User with this email already exists in this organization",
                "email",
                dto.getEmail()
            );
        }

        var groups = firstUser ? List.of("ADMINS") : List.of("USERS");

        // Create user in Keycloak first
        String keycloakUserId = createKeycloakUser(
            dto.getUsername(),
            dto.getEmail(),
            dto.getFullname(),
            organizationId,
            groups
        );

        // Create entity
        User user = mapper.toEntity(dto, organizationId);
        user.setId(UUID.fromString(keycloakUserId));

        user = repository.save(user);

        log.info("Created User: id={}, username={}, organization={}", user.getId(), user.getUsername(), organizationId);

        // Publish event
        var event = mapper.toUserCreated(user);
        eventPublisher.publishEvent(event);

        return mapper.toDto(user);
    }

    /**
     * Creates a user in Keycloak with the specified parameters.
     * This method eliminates code duplication between different user creation flows.
     */
    private String createKeycloakUser(
            String username,
            String email,
            String fullName,
            UUID organizationId,
            List<String> groups) {

        UserRepresentation user = getUserRepresentation(username, email);
        setNames(user, fullName, email);
        setCredentials(user);
        setOrganizationId(user, organizationId);
        user.setGroups(groups);

        // Create user in Keycloak realm
        try (Response response = keycloak.realm(properties.getRealm())
                .users()
                .create(user)) {

            if (response.getStatus() != 201) {
                String errorMessage = response.readEntity(String.class);
                log.error("Failed to create user in Keycloak. Status: {}, Error: {}", response.getStatus(), errorMessage);
                throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus());
            }

            // Extract created user ID from Location header
            String location = response.getLocation().toString();
            String userId = location.substring(location.lastIndexOf("/") + 1);
            
            log.info("User created successfully in Keycloak: username={}, organization-id={}, groups={}", 
                username, organizationId, groups);
            return userId;
        }
    }

    @Override
    public UserDto getById(UUID id, UUID organizationId) {
        User user = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));
        
        // Security: verify organization match
        if (!user.getChurchId().equals(organizationId)) {
            throw new NotFoundException("User", id);  // 404, not 403
        }
        
        return mapper.toDto(user);
    }

    @Override
    public UserDto getByEmail(String email) {
        return repository.findByEmail(email)
            .map(mapper::toDto)
            .orElse(null);
    }

    private static UserRepresentation getUserRepresentation(String username, String email) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(username);
        user.setEmail(email);
        user.setEmailVerified(true);

        return user;
    }

    private static void setNames(UserRepresentation user, String fullName, String userName) {
        // Handle fullName - split into firstName and lastName
        fullName = fullName != null && !fullName.isEmpty() ? fullName : userName;
        String firstName = fullName;
        String lastName = "";

        if (fullName.contains(" ")) {
            int spaceIndex = fullName.indexOf(" ");
            firstName = fullName.substring(0, spaceIndex);
            lastName = fullName.substring(spaceIndex + 1);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
    }

    private static void setCredentials(UserRepresentation user) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("password");
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));
    }

    private static void setOrganizationId(UserRepresentation user, UUID churchId) {
        // Set attributes - organization-id
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("organization-id", List.of(churchId.toString()));
        user.setAttributes(attributes);
    }
}