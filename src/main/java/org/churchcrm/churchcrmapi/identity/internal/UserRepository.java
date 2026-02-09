package org.churchcrm.churchcrmapi.identity.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User.
 * Internal to the module.
 */
@Repository
interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsernameAndChurchId(String username, UUID churchId);
    
    boolean existsByEmailAndChurchId(String email, UUID churchId);
    
    Optional<User> findByEmail(String email);
}