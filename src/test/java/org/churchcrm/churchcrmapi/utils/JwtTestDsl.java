package org.churchcrm.churchcrmapi.utils;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class JwtTestDsl {

    private UUID organizationId;
    private final List<String> roles = new ArrayList<>();

    public static JwtTestDsl jwt() {
        return new JwtTestDsl();
    }

    public JwtTestDsl organization(UUID organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public JwtTestDsl admin() {
        this.roles.add("ADMIN");
        return this;
    }

    public RequestPostProcessor build() {
        return org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> {
                    jwt.claim("organization_id", organizationId.toString());
                    jwt.claim("resource_access", Map.of(
                            "church-cms-api", Map.of("roles", roles)
                    ));
                })
                .authorities(
                        roles.stream()
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                                .toArray(SimpleGrantedAuthority[]::new)
                );
    }
}