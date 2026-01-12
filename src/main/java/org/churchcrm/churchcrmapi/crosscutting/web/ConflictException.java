package org.churchcrm.churchcrmapi.crosscutting.web;

import lombok.Getter;

/**
 * Base exception for conflict errors (HTTP 409 Conflict).
 * 
 * <p>Use this exception when:</p>
 * <ul>
 *   <li>A resource already exists (duplicate key/unique constraint violation)</li>
 *   <li>The current state conflicts with the requested operation</li>
 *   <li>Concurrent modification conflicts occur</li>
 *   <li>Business rules prevent the operation due to existing state</li>
 * </ul>
 * 
 * <p>Examples:</p>
 * <ul>
 *   <li>Email already registered</li>
 *   <li>Church hostname already taken</li>
 *   <li>Cannot delete church with active members</li>
 *   <li>Version mismatch in optimistic locking</li>
 * </ul>
 */
@Getter
public class ConflictException extends RuntimeException {
    
    private final String conflictType;
    private final Object conflictValue;
    
    /**
     * Creates a conflict exception with a message.
     * 
     * @param message the error message
     */
    public ConflictException(String message) {
        super(message);
        this.conflictType = null;
        this.conflictValue = null;
    }
    
    /**
     * Creates a conflict exception with details about the conflict.
     * 
     * @param message the error message
     * @param conflictType the type of conflict (e.g., "email", "hostname")
     * @param conflictValue the conflicting value
     */
    public ConflictException(String message, String conflictType, Object conflictValue) {
        super(message);
        this.conflictType = conflictType;
        this.conflictValue = conflictValue;
    }
    
    /**
     * Creates a conflict exception with a cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
        this.conflictType = null;
        this.conflictValue = null;
    }
}
