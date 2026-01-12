package org.churchcrm.churchcrmapi.crosscutting.auditing.internal;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorProvider implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        var auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if(auth == null || !auth.isAuthenticated()){
            return Optional.empty();
        }

        return Optional.of(auth.getName());
    }
}
