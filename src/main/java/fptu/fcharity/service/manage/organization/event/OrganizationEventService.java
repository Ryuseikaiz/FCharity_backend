package fptu.fcharity.service.manage.organization.event;

import fptu.fcharity.dto.organization.OrganizationEventDTO;
import fptu.fcharity.entity.OrganizationEvent;
import fptu.fcharity.entity.User;

import java.util.List;
import java.util.UUID;

public interface OrganizationEventService {
    List<OrganizationEventDTO> findByOrganizationId(UUID organizationId);
    OrganizationEventDTO save(OrganizationEventDTO organizationEventDTO, UUID organizationId);
    OrganizationEventDTO update(OrganizationEventDTO updatedOrganizationEventDTO);
    OrganizationEventDTO findByOrganizationEventId(UUID organizationEventId);
    boolean existsByOrganizationEventId(UUID organizationEventId);
    void deleteByOrganizationEventId(UUID organizationEventId);

    void sendEventInvitationEmail();
}
