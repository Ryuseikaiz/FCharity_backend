package fptu.fcharity.repository;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, UUID> {
    List<OrganizationMember> findOrganizationMemberByOrganization(Organization organization);
}
