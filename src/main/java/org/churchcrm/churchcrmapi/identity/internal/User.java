package org.churchcrm.churchcrmapi.identity.internal;

import jakarta.persistence.*;
import lombok.*;
import org.churchcrm.churchcrmapi.crosscutting.auditing.Audit;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

/**
 * User entity for managing church users.
 * 
 * ArchUnit Rules:
 * - Rule #1: Has @Embedded Audit field
 * - Rule #2: Uses UUID for @Id
 * - Rule #4: No inheritance (extends nothing)
 * - Rule #5: Audit field named "audit"
 * - Rule #6: Has @EntityListeners(AuditingEntityListener.class)
 * 
 * Primary key is Keycloak user ID to eliminate duplication and simplify design.
 */
@Entity
@Table(schema = "identity", name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Persistable<UUID> {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "church_id", nullable = false)
    private UUID churchId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "fullname", nullable = false, length = 200)
    private String fullname;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Embedded
    private Audit audit = new Audit();

    /**
     * Determines if this entity is new (not yet persisted).
     * 
     * Since the User ID is set externally from Keycloak before saving,
     * Spring Data cannot use the default ID-null check to determine if the entity is new.
     * We use the createdAt audit field instead: if it's null, the entity is new.
     * 
     * This is required for:
     * - JPA auditing (@CreatedDate, @LastModifiedDate) to work correctly
     * - Spring Data to call persist() instead of merge()
     */
    @Override
    public boolean isNew() {
        return audit == null || audit.getCreatedAt() == null;
    }
}