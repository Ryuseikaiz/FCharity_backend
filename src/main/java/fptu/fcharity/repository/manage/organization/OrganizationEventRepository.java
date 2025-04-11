package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.OrganizationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationEventRepository extends JpaRepository<OrganizationEvent, UUID> {
    @Query("SELECT oe FROM OrganizationEvent oe JOIN FETCH oe.organizer o JOIN FETCH o.walletAddress w JOIN FETCH o.ceo c WHERE o.organizationId = :organizationId")
    public List<OrganizationEvent> findOrganizationEventByOrganizerOrganizationId(@Param("organizationId") UUID organizationId);
    OrganizationEvent findOrganizationEventsByOrganizationEventId(UUID organizationEventId);
    boolean existsOrganizationEventByOrganizationEventId(UUID organizationEventId);
}
