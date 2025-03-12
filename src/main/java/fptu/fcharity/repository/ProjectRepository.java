package fptu.fcharity.repository;

import fptu.fcharity.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @EntityGraph(attributePaths = {"category","wallet"})
    Project findWithCategoryWalletById(UUID id);
    @EntityGraph(attributePaths = {"category","leader","organization"})
    Project findWithEssentialById(UUID id);
}
