package org.churchcrm.churchcrmapi.organization;

import java.util.UUID;

public record ChurchCreated(
        UUID churchId,
        String name,
        String userName,
        String email,
        String fullName
) {
}
