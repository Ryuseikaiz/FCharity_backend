package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.EventEmailAccess;
import fptu.fcharity.entity.OrganizationEvent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventEmailAccessRepository extends JpaRepository<EventEmailAccess, UUID> {
    @EntityGraph(attributePaths = {
            "organizationEvent",
            "organizationEvent.organizer",
            "organizationEvent.organizer.walletAddress",
            "organizationEvent.organizer.ceo"
    })
    List<EventEmailAccess> findEventEmailAccessByOrganizationEventOrganizationEventIdAndAccessType(UUID organizationEventOrganizationEventId, EventEmailAccess.AccessType accessType);

    @EntityGraph(attributePaths = {
            "organizationEvent",
            "organizationEvent.organizer",
            "organizationEvent.organizer.walletAddress",
            "organizationEvent.organizer.ceo"
    })
    List<EventEmailAccess> findEventEmailAccessByOrganizationEventOrganizationEventId(UUID organizationEventId);

}
