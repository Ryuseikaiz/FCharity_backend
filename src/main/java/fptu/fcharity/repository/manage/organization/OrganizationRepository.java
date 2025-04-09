package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.Organization;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface OrganizationRepository  extends JpaRepository<Organization, UUID> {
    @EntityGraph(attributePaths = {"ceo","ceo.walletAddress","walletAddress"})
    @Query("SELECT o FROM Organization o WHERE o.ceo.id = :UserId")
    Organization findOrganizationByUserId(UUID UserId);
}



