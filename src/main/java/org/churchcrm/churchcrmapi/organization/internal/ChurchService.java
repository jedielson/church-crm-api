package org.churchcrm.churchcrmapi.organization.internal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churchcrm.churchcrmapi.crosscutting.web.ConflictException;
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
    public ChurchDto createChurch(CreateChurchDto dto) {
        // Check hostname uniqueness
        if (repository.existsByHostName(dto.hostName())) {
            throw new ConflictException("Church with this hostname already exists", "hostname", dto.hostName());
        }

        var church = Church.create(dto, churchMapper);
        church = this.repository.save(church);
        log.info("Church created: {} with id: {}", church.getName(), church.getId());

        // Publish ChurchCreated event
        var event = churchMapper.toChurchCreated(church, dto);
        
        eventPublisher.publishEvent(event);
        log.info("ChurchCreated event published for church: {} by user: {}", church.getName(), dto.userName());

        return churchMapper.toDto(church);
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
