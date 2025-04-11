package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.Timeline;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TimelineRepository extends JpaRepository<Timeline, UUID> {
    @EntityGraph(attributePaths = {"project"})
    @Query("SELECT t FROM Timeline t where  t.project.id = :projectId")
    List<Timeline> findByProjectId(UUID projectId);
    @EntityGraph(attributePaths = {"project"})
    Timeline findWithEssentialById(UUID id);
    @EntityGraph(attributePaths = {"project"})
    @Query("SELECT t FROM Timeline t where  t.project.id = :projectId AND t.endTime IS NULL")
    List<Timeline> findOngoingPhaseByProjectId(UUID projectId);
}
