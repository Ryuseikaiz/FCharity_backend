package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.TaskPlanStatus;
import fptu.fcharity.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TaskPlanRepository extends JpaRepository<TaskPlan, UUID> {
    @EntityGraph(attributePaths = {"phase","status"})
    @Query("SELECT t FROM TaskPlan t where t.phase.id = :phaseId")
    List<TaskPlan> findByPhaseId(UUID phaseId);
    @EntityGraph(attributePaths = {"phase","status","user","parentTask"})
    @Query("SELECT t FROM TaskPlan t where t.id = :id")
    TaskPlan findWithEssentialById(UUID id);
    List<TaskPlan> findTaskPlanByStatus(TaskPlanStatus status);
    @EntityGraph(attributePaths = {"phase","status"})
    @Query("SELECT t FROM TaskPlan t where t.user.id = :userId")
    List<TaskPlan> findTaskPlanByUser(UUID userId);
    @EntityGraph(attributePaths = {"phase","status"})
    @Query("SELECT t FROM TaskPlan t where t.phase.project.id = :projectId")
    List<TaskPlan> findTaskPlanByProject(UUID projectId);
    @EntityGraph(attributePaths = {"phase","status"})
    @Query("SELECT t FROM TaskPlan t where t.parentTask.id= :taskId")
    List<TaskPlan> findByParentTaskId(UUID taskId);
}
