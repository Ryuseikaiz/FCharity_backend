package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, UUID> {
    List<ProjectImage> findByProjectId(UUID projectId);
    void deleteByProjectId(UUID projectId);
}
