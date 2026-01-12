package org.churchcrm.churchcrmapi.organization.internal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churchcrm.churchcrmapi.organization.ChurchCreated;
import org.churchcrm.churchcrmapi.organization.ChurchDto;
import org.churchcrm.churchcrmapi.organization.CreateChurchDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Service
class ChurchService {

    private final ApplicationEventPublisher eventPublisher;
    private final ChurchMapper churchMapper;
    private final ChurchRepository repository;

    @Transactional
    public void createChurch(CreateChurchDto dto) {
        var church = Church.create(dto, churchMapper);
        church = this.repository.save(church);
        log.info("Church created: {} with id: {}", church.getName(), church.getId());

        // Publish ChurchCreated event
        var event = new ChurchCreated(
                church.getId(),
                church.getName(),
                dto.userName(),
                dto.email(),
                "" // fullName - not in DTO yet
        );
        
        eventPublisher.publishEvent(event);
        log.info("ChurchCreated event published for church: {} by user: {}", church.getName(), dto.userName());
    }

    @Transactional(readOnly = true)
    public ChurchDto getChurchById(UUID churchId, UUID organizationId) {
        Church church = repository.findById(churchId)
                .orElseThrow(() -> new ChurchNotFoundException(churchId));

        // Security check: user's organization must match church ID
        // Return 404 to hide existence of churches that don't belong to the user
        if (!church.getId().equals(organizationId)) {
            log.debug("User with organization {} attempted to access church {}", organizationId, churchId);
            throw new ChurchNotFoundException(churchId);
        }

        return churchMapper.toDto(church);
    }
}
