package org.churchcrm.churchcrmapi.crosscutting.web;

import lombok.Getter;

/**
 * Base exception for forbidden access errors (HTTP 403 Forbidden).
 * 
 * <p>Use this exception when:</p>
 * <ul>
 *   <li>User is authenticated but lacks required permissions</li>
 *   <li>User doesn't have the required role</li>
 *   <li>Operation is not allowed for the user's current state</li>
 *   <li>Resource access is restricted by business rules</li>
 * </ul>
 * 
 * <p><strong>Security Note:</strong> When you want to hide resource existence from
 * unauthorized users, use {@link NotFoundException} instead (security through obscurity).
 * Use this exception only when you want to explicitly indicate that access is denied.</p>
 * 
 * <p>Examples:</p>
 * <ul>
 *   <li>User tries to delete a church (requires ADMIN role)</li>
 *   <li>User tries to modify another user's profile</li>
 *   <li>User account is suspended</li>
 * </ul>
 */
@Getter
public class ForbiddenException extends RuntimeException {
    
    private final String requiredPermission;
    private final String resource;
    
    /**
     * Creates a forbidden exception with a message.
     * 
     * @param message the error message
     */
    public ForbiddenException(String message) {
        super(message);
        this.requiredPermission = null;
        this.resource = null;
    }
    
    /**
     * Creates a forbidden exception with permission details.
     * 
     * @param message the error message
     * @param requiredPermission the permission required for the operation
     */
    public ForbiddenException(String message, String requiredPermission) {
        super(message);
        this.requiredPermission = requiredPermission;
        this.resource = null;
    }
    
    /**
     * Creates a forbidden exception with resource and permission details.
     * 
     * @param message the error message
     * @param resource the resource being accessed
     * @param requiredPermission the permission required
     */
    public ForbiddenException(String message, String resource, String requiredPermission) {
        super(message);
        this.requiredPermission = requiredPermission;
        this.resource = resource;
    }
}
