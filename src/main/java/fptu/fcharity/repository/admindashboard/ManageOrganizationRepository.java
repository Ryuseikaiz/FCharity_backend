package fptu.fcharity.repository.admindashboard;

import fptu.fcharity.entity.Organization;
//import fptu.fcharity.entity.Organization.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageOrganizationRepository extends JpaRepository<Organization, UUID> {
    List<Organization> findByOrganizationStatus(String status);
    Optional<Organization> findByEmail(String email);
}
