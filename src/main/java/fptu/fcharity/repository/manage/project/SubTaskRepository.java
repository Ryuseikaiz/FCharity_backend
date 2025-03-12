package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.SubTask;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, UUID> {
    @EntityGraph(attributePaths = {"taskPlan"})
    List<SubTask> findByTaskPlanId(UUID taskPlanId);
    @EntityGraph(attributePaths = {"taskPlan"})
    SubTask findWithIncludeById(UUID id);
    @EntityGraph(attributePaths = {"taskPlan","user"})
    @Query("SELECT r FROM SubTask r")
    List<SubTask> findAllWithInclude();
}
