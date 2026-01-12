package org.churchcrm.churchcrmapi.crosscutting.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web MVC configuration for registering custom argument resolvers.
 */
@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final OrganizationIdArgumentResolver organizationIdArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(organizationIdArgumentResolver);
    }
}
