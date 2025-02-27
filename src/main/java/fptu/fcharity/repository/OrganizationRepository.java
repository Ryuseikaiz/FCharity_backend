package fptu.fcharity.repository;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface OrganizationRepository  extends JpaRepository<Organization, UUID> {
}



