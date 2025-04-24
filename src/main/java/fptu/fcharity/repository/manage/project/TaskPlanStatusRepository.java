package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.TaskPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TaskPlanStatusRepository extends JpaRepository<TaskPlanStatus, UUID> {
    TaskPlanStatus findByStatusName(String name);
    @Query("SELECT t FROM TaskPlanStatus t where t.id = :id")
    TaskPlanStatus findWithEssentialById(UUID id);
    @Query("SELECT t FROM TaskPlanStatus t " +
            "JOIN FETCH t.phase p " +       // Idiomatic join: TaskPlan -> Timeline (p)
            "JOIN FETCH p.project pr " +    // Idiomatic join: Timeline (p) -> Project (pr)
            "WHERE pr.id = :projectId")
    List<TaskPlanStatus> findAllByProjectId(UUID projectId);
}
