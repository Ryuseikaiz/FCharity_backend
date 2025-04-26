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
    List<OrganizationMemberDTO> findAll();
    List<UserDTO> getAllUsersNotInOrganization(UUID organizationId);
    OrganizationMemberDTO findById(UUID id);
    OrganizationMemberRole findUserRoleInOrganization(UUID userId, UUID organizationId);
    List<OrganizationMemberDTO> findOrganizationMemberByOrganization(Organization organization);
    List<OrganizationMemberDTO> findOrganizationMemberByOrganizationId(UUID organizationId);
    OrganizationMemberDTO createOrganizationMember(UUID organizationId, UUID userId);
    OrganizationMemberDTO updateRole(OrganizationMemberDTO organizationMemberDTO);
    void delete(UUID membershipId);
}
