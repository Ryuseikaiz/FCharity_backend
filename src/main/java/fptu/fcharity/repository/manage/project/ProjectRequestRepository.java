package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.ProjectRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProjectRequestRepository extends JpaRepository<ProjectRequest, UUID> {
     @EntityGraph(attributePaths = {"user", "project"})
     @Query("SELECT p FROM ProjectRequest p WHERE p.id = :id")
     ProjectRequest findWithEssentialById(UUID id);
     @EntityGraph(attributePaths = {"user", "project"})
     @Query("SELECT p FROM ProjectRequest p")
     List<ProjectRequest> findWithEssentialAll();
     @EntityGraph(attributePaths = {"user", "project"})
     @Query("SELECT p FROM ProjectRequest p WHERE p.user.id = :userId")
     List<ProjectRequest> findWithEssentialByUserId(UUID userId);
     @EntityGraph(attributePaths = {"user", "project"})
     @Query("SELECT p FROM ProjectRequest p WHERE p.user.id = :userId AND p.project.id = :projectId AND p.requestType = :requestType AND p.status = 'PENDING'")
     List<ProjectRequest> findExistingRequestByUserIdAndProjectId(UUID userId,UUID projectId,String requestType);
     @EntityGraph(attributePaths = {"user", "project"})
     @Query("SELECT p FROM ProjectRequest p WHERE p.project.id = :projectId")
     List<ProjectRequest> findWithEssentialByProjectId(UUID projectId);
}
