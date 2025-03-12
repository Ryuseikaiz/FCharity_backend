package fptu.fcharity.repository;

import fptu.fcharity.entity.Project;
//import fptu.fcharity.entity.Project.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByProjectStatus(String status);
    Optional<Project> findById(UUID projectId);
}
