package fptu.fcharity.repository.manage.request;

import fptu.fcharity.entity.Request;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
    @EntityGraph(attributePaths = {"category","user"})
    Request findWithIncludeById(UUID id);
    @EntityGraph(attributePaths = {"category","user"})
    @Query("SELECT r FROM Request r")
    List<Request> findAllWithInclude();
    @Query("SELECT r FROM Request r WHERE r.user.id = :userId")
    @EntityGraph(attributePaths = {"category","user"})
    List<Request> findByUserId(@Param("userId") UUID userId);
}