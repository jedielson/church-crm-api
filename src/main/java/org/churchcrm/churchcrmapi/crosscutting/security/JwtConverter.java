package org.churchcrm.churchcrmapi.crosscutting.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract roles from both church-cms-api and church-cms-ui clients
        var roles = getRoles(jwt, List.of("church-cms-api", "church-cms-ui"));

        var grants = roles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        return new JwtAuthenticationToken(jwt, grants);
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getRoles(Jwt jwt, List<String> clientIds) {
        var roles = new ArrayList<String>();
        
        // Get resource_access claim
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        
        if (resourceAccess == null) {
            return roles;
        }
        
        // Extract roles from each specified client
        for (String clientId : clientIds) {
            Object clientResource = resourceAccess.get(clientId);
            
            if (clientResource instanceof Map) {
                Map<String, Object> clientMap = (Map<String, Object>) clientResource;
                Object rolesObj = clientMap.get("roles");
                
                if (rolesObj instanceof Collection) {
                    roles.addAll((Collection<String>) rolesObj);
                }
            }
        }

        return roles;
    }
}
