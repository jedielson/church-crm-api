package org.churchcrm.churchcrmapi.identity.internal;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churchcrm.churchcrmapi.identity.internal.config.KeycloakProperties;
import org.churchcrm.churchcrmapi.organization.ChurchCreated;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IUserService {
    void createUser(ChurchCreated event);
}

@Service
@RequiredArgsConstructor
@Slf4j
class UserService implements IUserService {

    private final Keycloak keycloak;
    private final KeycloakProperties properties;

    @Override
    public void createUser(ChurchCreated event) {

        UserRepresentation user = getUserRepresentation(event);
        setNames(user, event);
        setCredentials(user);
        setOrganizationId(event, user);
        setGroups(user);

        // Create user in Keycloak realm
        try (Response response = keycloak.realm(properties.getRealm())
                .users()
                .create(user)) {

            validateResponse(event, response);
        }
    }

    private static void validateResponse(ChurchCreated event, Response response) {

        if (response.getStatus() != 201) {
            String errorMessage = response.readEntity(String.class);
            log.error(
                    "Failed to create user in Keycloak. Status: {}, Error: {}",
                    response.getStatus(),
                    errorMessage);
            throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus());
        }

        log.info(
                "User created successfully in Keycloak: email={}, organization-id={}",
                event.email(),
                event.churchId());
    }

    private static void setGroups(UserRepresentation user) {
        user.setGroups(List.of("USERS"));
    }

    private static void setOrganizationId(ChurchCreated event, UserRepresentation user) {
        // Set attributes - organization-id
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("organization-id", List.of(event.churchId().toString()));
        user.setAttributes(attributes);
    }

    private static UserRepresentation getUserRepresentation(ChurchCreated event) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(event.email());
        user.setEmail(event.email());
        user.setEmailVerified(true);

        return user;
    }

    private static void setNames(UserRepresentation user, ChurchCreated event) {
        // Handle fullName - split into firstName and lastName
        String fullName = event.fullName() != null && !event.fullName().isEmpty() ? event.fullName() : event.userName();
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
}
