package org.churchcrm.churchcrmapi.organization.internal;

import lombok.Getter;
import org.churchcrm.churchcrmapi.crosscutting.web.NotFoundException;

import java.util.UUID;

/**
 * Exception thrown when a church is not found or when a user attempts to access
 * a church that doesn't belong to their organization.
 * 
 * <p>For security reasons, this exception is used for both scenarios to prevent
 * information leakage about valid church IDs (security through obscurity).</p>
 * 
 * <p>This exception extends {@link NotFoundException} and will return HTTP 404.</p>
 */
@Getter
public class ChurchNotFoundException extends NotFoundException {
    
    private final UUID churchId;
    
    /**
     * Creates a church not found exception.
     * 
     * @param churchId the ID of the church that was not found
     */
    public ChurchNotFoundException(UUID churchId) {
        super("Church", churchId);
        this.churchId = churchId;
    }
}
