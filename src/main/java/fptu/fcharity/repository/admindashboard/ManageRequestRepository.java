package fptu.fcharity.repository.admindashboard;

import fptu.fcharity.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageRequestRepository extends JpaRepository<Request, UUID> {
    List<Request> findByStatus(String status);
    Optional<Request> findByEmail(String email);
}
