package fptu.fcharity.service.manage.organization.event;

import fptu.fcharity.dto.organization.OrganizationEventDTO;
import fptu.fcharity.entity.OrganizationEvent;

import java.util.List;
import java.util.UUID;

public interface OrganizationEventService {
    List<OrganizationEventDTO> findByOrganizationId(UUID organizationId);
    OrganizationEvent save(OrganizationEventDTO organizationEventDTO);
    OrganizationEvent update(OrganizationEventDTO updatedOrganizationEventDTO);
    OrganizationEvent findByOrganizationEventId(UUID organizationEventId);
    boolean existsByOrganizationEventId(UUID organizationEventId);
    void deleteByOrganizationEventId(UUID organizationEventId);
}
