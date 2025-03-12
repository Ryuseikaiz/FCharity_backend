package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.ProjectMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    @EntityGraph(attributePaths = {"project", "user"})
    List<ProjectMember> findByProjectId(UUID projectId);
    List<ProjectMember> findByUserId(UUID userId);
    List<ProjectMember> findByProjectIdAndUserId(UUID projectId, UUID userId);
    @EntityGraph(attributePaths = {"project", "user"})

    ProjectMember findWithEssentialById(UUID id);
    @EntityGraph(attributePaths = {"project","user"})
    @Query("SELECT r FROM ProjectMember r")
    List<ProjectMember> findAllWithInclude();
}
