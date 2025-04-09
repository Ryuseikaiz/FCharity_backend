package fptu.fcharity.service.manage.organization.event;

import fptu.fcharity.dto.organization.OrganizationEventDTO;
import fptu.fcharity.entity.OrganizationEvent;
import fptu.fcharity.repository.manage.organization.OrganizationEventRepository;
import fptu.fcharity.utils.mapper.organization.OrganizationEventMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationEventServiceImpl implements OrganizationEventService {
    private final OrganizationEventRepository organizationEventRepository;
    private final OrganizationEventMapper organizationEventMapper;

    public OrganizationEventServiceImpl(OrganizationEventRepository organizationEventRepository, OrganizationEventMapper organizationEventMapper) {
        this.organizationEventRepository = organizationEventRepository;
        this.organizationEventMapper = organizationEventMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationEventDTO> findByOrganizationId(UUID organizationId) {
        return organizationEventRepository
                .findOrganizationEventByOrganizerOrganizationId(organizationId)
                .stream()
                .map(organizationEventMapper::toDTO)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public OrganizationEvent save(OrganizationEvent organizationEvent) {
        return organizationEventRepository.save(organizationEvent);
    }

    @Override
    public OrganizationEvent findByOrganizationEventId(UUID organizationEventId) {
        return organizationEventRepository.findOrganizationEventsByOrganizationEventId((organizationEventId));
    }

    @Override
    public boolean existsByOrganizationEventId(UUID organizationEventId) {
        if (organizationEventRepository.existsOrganizationEventByOrganizationEventId(organizationEventId)) return true;
        return false;
    }

    @Override
    public void deleteByOrganizationEventId(UUID organizationEventId) {
        organizationEventRepository.deleteById((organizationEventId));
    }
}
