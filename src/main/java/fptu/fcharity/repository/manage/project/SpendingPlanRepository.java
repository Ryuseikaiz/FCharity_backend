package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.SpendingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Repository
public interface SpendingPlanRepository extends JpaRepository<SpendingPlan, UUID> {
   @Query("SELECT sp FROM SpendingPlan sp JOIN FETCH sp.project p WHERE p.id = :projectId")
    List<SpendingPlan> findByProjectId(UUID projectId);
    // Define any custom query methods if needed
    // For example:
    // List<SpendingPlan> findByProjectId(UUID projectId);
}
