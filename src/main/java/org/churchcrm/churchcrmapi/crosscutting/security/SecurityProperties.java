package org.churchcrm.churchcrmapi.crosscutting.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security")
public record SecurityProperties(boolean enabled) {
}
