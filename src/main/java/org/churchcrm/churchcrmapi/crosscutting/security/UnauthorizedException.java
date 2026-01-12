package org.churchcrm.churchcrmapi.crosscutting.security;

import org.churchcrm.churchcrmapi.crosscutting.web.ForbiddenException;

/**
 * Exception thrown when authentication fails or when required JWT claims are missing/invalid.
 * 
 * <p>This is a standalone exception that returns HTTP 401 Unauthorized.</p>
 * 
 * <p>Use this exception when:</p>
 * <ul>
 *   <li>JWT token is missing or invalid</li>
 *   <li>Required claims are missing from the token</li>
 *   <li>Token has expired</li>
 *   <li>Token signature is invalid</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> This is different from {@link ForbiddenException}, which
 * indicates the user is authenticated but lacks permissions (HTTP 403).</p>
 */
public class UnauthorizedException extends RuntimeException {
    
    /**
     * Creates an unauthorized exception with a message.
     * 
     * @param message the error message
     */
    public UnauthorizedException(String message) {
        super(message);
    }
    
    /**
     * Creates an unauthorized exception with a message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
