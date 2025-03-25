package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.TaskPlan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskPlanRepository extends JpaRepository<TaskPlan, UUID> {
    @EntityGraph(attributePaths = {"project"})
    TaskPlan findWithProjectById(UUID id);
    @EntityGraph(attributePaths = {"project","user"})
    TaskPlan findWithIncludeById(UUID id);
    @EntityGraph(attributePaths = {"project"})
    @Query("SELECT r FROM TaskPlan r")
    List<TaskPlan> findAllWithInclude();
    @EntityGraph(attributePaths = {"project"})
    List<TaskPlan> findByProjectId(UUID projectId);
}
