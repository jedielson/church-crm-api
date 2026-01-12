package org.churchcrm.churchcrmapi.crosscutting.web;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Base exception for validation errors (HTTP 400 Bad Request).
 * 
 * <p>Use this exception when input validation fails, including:</p>
 * <ul>
 *   <li>Invalid field values</li>
 *   <li>Missing required fields</li>
 *   <li>Format validation errors</li>
 *   <li>Business rule validation failures</li>
 * </ul>
 * 
 * <p>This exception supports field-level error details for precise error reporting.</p>
 */
@Getter
public class ValidationException extends RuntimeException {
    
    private final Map<String, String> fieldErrors;
    
    /**
     * Creates a validation exception with a general message.
     * 
     * @param message the error message
     */
    public ValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
    }
    
    /**
     * Creates a validation exception with a message and field-specific errors.
     * 
     * @param message the general error message
     * @param fieldErrors map of field names to error messages
     */
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : new HashMap<>();
    }
    
    /**
     * Creates a validation exception for a single field.
     * 
     * @param message the general error message
     * @param fieldName the name of the invalid field
     * @param fieldError the field-specific error message
     */
    public ValidationException(String message, String fieldName, String fieldError) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(fieldName, fieldError);
    }
    
    /**
     * Checks if this exception has field-specific errors.
     * 
     * @return true if field errors exist
     */
    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }
}
