package fptu.fcharity.repository.manage.request;


import fptu.fcharity.entity.HelpRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<HelpRequest, UUID> {
    @EntityGraph(attributePaths = {"category","user","user.walletAddress"})
    HelpRequest findWithIncludeById(UUID id);
    @EntityGraph(attributePaths = {"category","user","user.walletAddress"})
    @Query("SELECT r FROM HelpRequest r")
    List<HelpRequest> findAllWithInclude();
    @Query("SELECT r FROM HelpRequest r WHERE r.user.id = :userId")
    @EntityGraph(attributePaths = {"category","user","user.walletAddress"})
    List<HelpRequest> findByUserId(@Param("userId") UUID userId);
}