package org.churchcrm.churchcrmapi.organization.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface ChurchRepository extends JpaRepository<Church, UUID> {
}
