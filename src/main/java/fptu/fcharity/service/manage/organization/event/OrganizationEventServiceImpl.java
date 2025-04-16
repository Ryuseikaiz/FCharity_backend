package fptu.fcharity.service.manage.organization.event;

import ch.qos.logback.classic.spi.IThrowableProxy;
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
    public OrganizationEvent save(OrganizationEventDTO organizationEventDTO) {

        Organization organizer = organizationRepository
                .findById(organizationEventDTO.getOrganizer().getOrganizationId())
                .orElseThrow(() -> new ApiRequestException("Organization not found"));
        OrganizationEvent event = new OrganizationEvent();

        event.setTitle(organizationEventDTO.getTitle());
        event.setStartTime(organizationEventDTO.getStartTime());
        event.setEndTime(organizationEventDTO.getEndTime());
        event.setBackgroundColor(organizationEventDTO.getBackgroundColor());
        event.setBorderColor(organizationEventDTO.getBorderColor());
        event.setTextColor(organizationEventDTO.getTextColor());
        event.setLocation(organizationEventDTO.getLocation());
        event.setMeetingLink(organizationEventDTO.getMeetingLink());
        event.setEventType(organizationEventDTO.getEventType());
        event.setOrganizer(organizer);
        event.setTargetAudience(organizationEventDTO.getTargetAudience());
        event.setSummary(organizationEventDTO.getSummary());
        event.setFullDescription(organizationEventDTO.getFullDescription());

        return organizationEventRepository.save(event);
    }

    @Override
    @Transactional
    public OrganizationEvent update(OrganizationEventDTO updatedOrganizationEventDTO) {
        OrganizationEvent event = organizationEventRepository
                .findById(updatedOrganizationEventDTO.getOrganizationEventId())
                .orElseThrow(()-> new ApiRequestException("Event not found"));

        Organization organizer = organizationRepository
                .findById(updatedOrganizationEventDTO.getOrganizer().getOrganizationId())
                .orElseThrow(() -> new ApiRequestException("Organization not found"));

        event.setTitle(updatedOrganizationEventDTO.getTitle());
        event.setStartTime(updatedOrganizationEventDTO.getStartTime());
        event.setEndTime(updatedOrganizationEventDTO.getEndTime());
        event.setBackgroundColor(updatedOrganizationEventDTO.getBackgroundColor());
        event.setBorderColor(updatedOrganizationEventDTO.getBorderColor());
        event.setTextColor(updatedOrganizationEventDTO.getTextColor());
        event.setLocation(updatedOrganizationEventDTO.getLocation());
        event.setMeetingLink(updatedOrganizationEventDTO.getMeetingLink());
        event.setEventType(updatedOrganizationEventDTO.getEventType());
        event.setOrganizer(organizer);
        event.setTargetAudience(updatedOrganizationEventDTO.getTargetAudience());
        event.setSummary(updatedOrganizationEventDTO.getSummary());
        event.setFullDescription(updatedOrganizationEventDTO.getFullDescription());

        return organizationEventRepository.save(event);
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
