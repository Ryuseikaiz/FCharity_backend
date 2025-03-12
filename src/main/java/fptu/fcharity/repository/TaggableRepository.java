package fptu.fcharity.repository;

import fptu.fcharity.entity.Taggable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaggableRepository extends JpaRepository<Taggable, UUID> {
    @EntityGraph(attributePaths = { "tag"})
    @Query("SELECT r FROM Taggable r")
    List<Taggable> findAllWithInclude();
}