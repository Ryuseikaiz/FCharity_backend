package fptu.fcharity.service.manage.organization.event;

import fptu.fcharity.dto.organization.OrganizationEventDTO;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationEvent;
import fptu.fcharity.repository.manage.organization.OrganizationEventRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
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
    private final OrganizationRepository organizationRepository;

    public OrganizationEventServiceImpl(OrganizationEventRepository organizationEventRepository, OrganizationEventMapper organizationEventMapper, OrganizationRepository organizationRepository) {
        this.organizationEventRepository = organizationEventRepository;
        this.organizationEventMapper = organizationEventMapper;
        this.organizationRepository = organizationRepository;
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
    public OrganizationEventDTO save(OrganizationEventDTO organizationEventDTO, UUID organizationId) {
        System.out.println("🤖🤖🤖 Creating organization event: " + organizationEventDTO);
        Organization organizer = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new ApiRequestException("Organization not found"));
        OrganizationEvent event = organizationEventMapper.toEntity(organizationEventDTO);
        event.setOrganizer(organizer);

        return organizationEventMapper.toDTO(organizationEventRepository.save(event));
    }

    @Override
    @Transactional
    public OrganizationEventDTO update(OrganizationEventDTO updatedOrganizationEventDTO) {
        OrganizationEvent event = organizationEventRepository
                .findById(updatedOrganizationEventDTO.getOrganizationEventId())
                .orElseThrow(()-> new ApiRequestException("Event not found"));

        Organization organizer = organizationRepository
                .findById(updatedOrganizationEventDTO.getOrganizer().getOrganizationId())
                .orElseThrow(() -> new ApiRequestException("Organization not found"));

        System.out.println("update in service: 🍎🍎 " + updatedOrganizationEventDTO);

        return organizationEventMapper.toDTO(organizationEventRepository.save(organizationEventMapper.toEntity(updatedOrganizationEventDTO)));
    }

    @Override
    public OrganizationEventDTO findByOrganizationEventId(UUID organizationEventId) {
        return organizationEventMapper.toDTO(organizationEventRepository.findOrganizationEventsByOrganizationEventId((organizationEventId)));
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
