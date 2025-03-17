package fptu.fcharity.repository;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, UUID> {
    List<OrganizationMember> findOrganizationMemberByOrganization(Organization organization);
}
