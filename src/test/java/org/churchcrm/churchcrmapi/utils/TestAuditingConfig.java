package org.churchcrm.churchcrmapi.utils;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Test configuration for JPA auditing in @ApplicationModuleTest context.
 *
 * The standard JpaAuditingConfig from crosscutting.auditing is not loaded
 * by @ApplicationModuleTest due to module boundary restrictions. This test
 * configuration provides the same auditing setup within the test context.
 */
@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "testAuditorProvider")
public class TestAuditingConfig {

    @Bean("testAuditorProvider")
    public AuditorAware<String> auditorProvider() {
        return () -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return Optional.of("system"); // Default auditor for async events
            }
            return Optional.of(auth.getName());
        };
    }
}
