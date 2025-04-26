package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.ProjectWithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ProjectWithdrawRequestRepository extends JpaRepository<ProjectWithdrawRequest, UUID> {
    @Query("SELECT p FROM ProjectWithdrawRequest p " +
            "JOIN FETCH Project pp on p.project.id = pp.id WHERE pp.id = :id")
    ProjectWithdrawRequest findByProject_Id(UUID id);
    // Define any custom query methods if needed
}
