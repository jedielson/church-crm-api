package org.churchcrm.churchcrmapi.crosscutting.web;

import lombok.Getter;

/**
 * Base exception for resource not found errors (HTTP 404 Not Found).
 * 
 * <p>Use this exception when:</p>
 * <ul>
 *   <li>A requested resource does not exist</li>
 *   <li>A user attempts to access a resource they don't own (security through obscurity)</li>
 *   <li>An invalid ID is provided</li>
 * </ul>
 * 
 * <p><strong>Security Note:</strong> For authorization failures, consider returning 404
 * instead of 403 to avoid leaking information about resource existence.</p>
 */
@Getter
public class NotFoundException extends RuntimeException {
    
    private final String resourceType;
    private final Object resourceId;
    
    /**
     * Creates a not found exception with a message.
     * 
     * @param message the error message
     */
    public NotFoundException(String message) {
        super(message);
        this.resourceType = null;
        this.resourceId = null;
    }
    
    /**
     * Creates a not found exception with resource details.
     * 
     * @param resourceType the type of resource (e.g., "Church", "User")
     * @param resourceId the ID of the resource
     */
    public NotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s not found: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    /**
     * Creates a not found exception with a custom message and resource details.
     * 
     * @param message the custom error message
     * @param resourceType the type of resource
     * @param resourceId the ID of the resource
     */
    public NotFoundException(String message, String resourceType, Object resourceId) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
}
