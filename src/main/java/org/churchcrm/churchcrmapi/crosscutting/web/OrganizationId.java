package org.churchcrm.churchcrmapi.crosscutting.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method parameter that should be resolved from the JWT's organization_id claim.
 * The parameter type must be UUID.
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @GetMapping("/{id}")
 * public ChurchDto getChurch(
 *     @PathVariable UUID id,
 *     @OrganizationId UUID organizationId
 * ) {
 *     return churchService.getChurch(id, organizationId);
 * }
 * }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OrganizationId {
}
