package org.churchcrm.churchcrmapi.crosscutting.security;

import org.churchcrm.churchcrmapi.crosscutting.web.OrganizationId;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

/**
 * Resolves method parameters annotated with {@link OrganizationId} by extracting
 * the organization_id claim from the JWT token.
 * 
 * <p>This resolver ensures that the organization context is automatically injected
 * into controller methods, enabling organization-scoped access control.</p>
 */
@Component
public class OrganizationIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(OrganizationId.class) 
            && parameter.getParameterType().equals(UUID.class);
    }

    @Override
    @Nullable
    public Object resolveArgument(
            @Nullable MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @Nullable NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            throw new UnauthorizedException("Invalid authentication token");
        }
        
        Jwt jwt = jwtAuthenticationToken.getToken();
        String organizationId = jwt.getClaimAsString("organization_id");
        
        if (organizationId == null || organizationId.isBlank()) {
            throw new UnauthorizedException("Missing organization_id claim in JWT token");
        }
        
        try {
            return UUID.fromString(organizationId);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid organization_id format: " + organizationId, e);
        }
    }
}
