package org.churchcrm.churchcrmapi.crosscutting.auditing;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Embeddable audit information for tracking entity creation and modification.
 * 
 * This class uses the experimental approach of placing @EntityListeners on the @Embeddable class itself.
 * If this doesn't work, entities will need to add @EntityListeners(AuditingEntityListener.class) themselves.
 */
@Getter
@Setter
@Embeddable
@EntityListeners(AuditingEntityListener.class)
public class Audit {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 255)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 255)
    private String updatedBy;
}
