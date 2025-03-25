package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.ProjectRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ProjectRequestRepository extends JpaRepository<ProjectRequest, UUID> {
     ProjectRequest findByProjectIdAndUserId(UUID projectId, UUID userId);
}
