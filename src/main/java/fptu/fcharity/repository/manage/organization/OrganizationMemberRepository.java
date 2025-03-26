package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, UUID> {
    List<OrganizationMember> findOrganizationMemberByOrganization(Organization organization);

    OrganizationMember findOrganizationMemberByUserIdAndOrganizationOrganizationId(UUID userId, UUID organizationId);

    Optional<OrganizationMember> findOrganizationMemberByMembershipId(UUID membershipId);

    List<OrganizationMember> findOrganizationMemberByUserId(UUID managerId);
}
