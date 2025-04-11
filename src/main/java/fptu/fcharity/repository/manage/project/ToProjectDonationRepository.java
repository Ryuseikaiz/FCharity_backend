package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.ToProjectDonation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ToProjectDonationRepository extends JpaRepository<ToProjectDonation, UUID> {
   @EntityGraph(attributePaths = {"user","project","project.walletAddress"})
    List<ToProjectDonation> findByProjectId(UUID projectId);
    @Query("SELECT p FROM ToProjectDonation p " +
            "JOIN FETCH p.user u " +
            "JOIN FETCH p.project pr " +
            "JOIN FETCH pr.walletAddress pw " +
            "WHERE p.id = :id")
    ToProjectDonation findWithEssentialById(UUID id);

    <T> ToProjectDonation findByOrderCode(int orderCode);

    // Custom query methods can be defined here if needed
    // For example, you can define methods to find donations by project ID or user ID
}
