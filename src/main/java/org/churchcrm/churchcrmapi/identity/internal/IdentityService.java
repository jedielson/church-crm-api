package org.churchcrm.churchcrmapi.identity.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churchcrm.churchcrmapi.organization.ChurchCreated;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityService {

    private final IUserService userService;

    @ApplicationModuleListener
    public void on(ChurchCreated event) {
        log.info("Creating church user for event {}: Thread: {}", event, Thread.currentThread().getName());
        
        try {
            userService.createUser(event);
        } catch (Exception e) {
            log.error("Failed to create user in Keycloak for church: {}", event.name(), e);
            throw new RuntimeException("Failed to create user in Keycloak", e);
        }
    }
}
