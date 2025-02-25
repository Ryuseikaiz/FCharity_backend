package fptu.fcharity.repository;

import fptu.fcharity.entity.Request;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
    @EntityGraph(attributePaths = {"category", "tag","user"})
    Request findWithCategoryAndTagById(UUID id);
}