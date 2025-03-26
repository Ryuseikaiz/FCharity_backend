package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TimelineRepository extends JpaRepository<Timeline, UUID> {
    List<Timeline> findByProjectId(UUID projectId);
}
