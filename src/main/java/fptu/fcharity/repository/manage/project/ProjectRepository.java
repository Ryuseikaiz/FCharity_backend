package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @EntityGraph(attributePaths = {"category","wallet"})
    Project findWithCategoryWalletById(UUID id);
    @EntityGraph(attributePaths = {"category", "leader", "organization","request","walletAddress"})
    @Query("SELECT p FROM Project p WHERE p.id = :id")
    Project findWithEssentialById(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"category","leader","leader.walletAddress","organization","request","walletAddress"})
    @Query("SELECT r FROM Project r where r.projectStatus != 'BANNED'")
    List<Project> findAllWithInclude();
    @EntityGraph(attributePaths = {"category","leader","organization","request","walletAddress"})
    @Query("SELECT r FROM Project r where r.leader.id = :userId")
    Project findMyOwnerProject(UUID userId);
}
