package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.ProjectExtraFundRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectExtraFundRequestRepository extends JpaRepository<ProjectExtraFundRequest, UUID> {
  @Query("SELECT p FROM ProjectExtraFundRequest p WHERE p.id = :id")
   ProjectExtraFundRequest findEssentialById(UUID id);

    @EntityGraph(attributePaths = {
          "organization",
          "project",
          "organization.walletAddress",
            "organization.ceo"
    })
    List<ProjectExtraFundRequest> findProjectExtraFundRequestByOrganizationOrganizationIdAndStatus(UUID organizationOrganizationId, String status);
}
