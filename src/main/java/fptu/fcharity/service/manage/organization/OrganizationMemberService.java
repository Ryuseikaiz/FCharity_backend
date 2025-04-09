package fptu.fcharity.service.manage.organization;
import fptu.fcharity.dto.organization.OrganizationMemberDTO;
import fptu.fcharity.dto.organization.UserDTO;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;
import fptu.fcharity.entity.User;

public interface OrganizationMemberService {
    List<OrganizationMember> findAll();
    List<UserDTO> getAllUsersNotInOrganization(UUID organizationId);
    Optional<OrganizationMember> findById(UUID id);
    OrganizationMemberRole findUserRoleInOrganization(UUID userId, UUID organizationId);
    List<OrganizationMember> findOrganizationMemberByOrganization(Organization organization);
    List<OrganizationMemberDTO> findOrganizationMemberByOrganizationId(UUID organizationId);
    OrganizationMember save(OrganizationMember organizationMember);
    OrganizationMember update(OrganizationMember organizationMember);
    void delete(UUID id);
}
