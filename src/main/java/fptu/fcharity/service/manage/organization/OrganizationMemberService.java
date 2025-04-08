package fptu.fcharity.service.manage.organization;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;
import fptu.fcharity.response.organization.OrganizationMemberResponse;

public interface OrganizationMemberService {
    List<OrganizationMember> findAll();
    Optional<OrganizationMember> findById(UUID id);
    OrganizationMemberRole findUserRoleInOrganization(UUID userId, UUID organizationId);
//    List<OrganizationMemberResponse> findOrganizationMemberByOrganizationId(Organization id);
    List<OrganizationMemberResponse> findOrganizationMemberByOrganizationId(UUID organizationId);
    OrganizationMember save(OrganizationMember organizationMember);
    OrganizationMember update(OrganizationMember organizationMember);
    void delete(UUID id);
}
