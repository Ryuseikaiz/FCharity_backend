package fptu.fcharity.service.manage.organization;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;

public interface OrganizationMemberService {
    List<OrganizationMember> findAll();
    Optional<OrganizationMember> findById(UUID id);
    OrganizationMemberRole findUserRoleInOrganization(UUID userId, UUID organizationId);
    List<OrganizationMember> findOrganizationMemberByOrganization(Organization organization);

    OrganizationMember save(OrganizationMember organizationMember);
    OrganizationMember update(OrganizationMember organizationMember);
    void delete(UUID id);
}
