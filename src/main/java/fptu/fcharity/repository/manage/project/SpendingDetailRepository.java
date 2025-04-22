package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.SpendingDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpendingDetailRepository extends JpaRepository<SpendingDetail, UUID> {
    // Define any custom query methods if needed
    // For example:
    // List<SpendingDetail> findBySpendingPlanId(UUID spendingPlanId);
    @EntityGraph(attributePaths = {"spendingItem", "project"})
    List<SpendingDetail> findByProjectId(UUID projectId);
}
