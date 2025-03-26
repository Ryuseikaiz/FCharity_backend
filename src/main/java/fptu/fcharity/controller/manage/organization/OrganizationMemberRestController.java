package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationMemberDTO;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.entity.User;
import fptu.fcharity.service.manage.organization.OrganizationMemberService;
import fptu.fcharity.service.manage.organization.OrganizationService;
import fptu.fcharity.service.manage.user.UserService;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrganizationMemberRestController {
    private final OrganizationMemberService organizationMemberService;
    private final OrganizationService organizationService;
    private final UserService userService;

    @Autowired
    public OrganizationMemberRestController(OrganizationMemberService organizationMemberService, OrganizationService organizationService, UserService userService) {
        this.organizationMemberService = organizationMemberService;
        this.organizationService = organizationService;
        this.userService = userService;
    }

    @GetMapping("/organization_members")
    public List<OrganizationMember> getOrganizationMembers() {
        return organizationMemberService.findAll();
    }

    @GetMapping("/organization-members/{organization_id}")
    public List<OrganizationMember> getOrganizationMember(@PathVariable UUID organization_id) {
        return organizationMemberService.findOrganizationMemberByOrganization(organizationService.getById(organization_id));
    }

    @PostMapping("/organization_members")
    public OrganizationMember createOrganizationMember(@RequestBody OrganizationMemberDTO organizationMemberDTO) {
        OrganizationMember organizationMember = new OrganizationMember();
        organizationMember.setOrganization(organizationService.getById(organizationMemberDTO.getOrganizationId()));
        organizationMember.setUser(userService.getById(organizationMemberDTO.getUserId()).orElseThrow(() -> new ApiRequestException("User not found")));
        System.out.println(organizationMember);

        return organizationMemberService.save(organizationMember);
    }

    @PutMapping("/organization_members")
    public ResponseEntity<?> updateOrganizationMember(@RequestBody OrganizationMember organizationMember, Authentication authentication) {
        OrganizationMember currentOrganizationMemberInfo = organizationMemberService.findById(organizationMember.getMembershipId()).orElseThrow(()-> new ApiRequestException("Member not found"));
        User authUser  = userService.findUserByEmail(authentication.getName());

        if (!Objects.equals(organizationMember.getMemberRole(), currentOrganizationMemberInfo.getMemberRole())) {
            OrganizationMemberRole authRole = organizationMemberService.findUserRoleInOrganization(authUser.getId(), organizationMember.getOrganization().getOrganizationId());
            if (authRole == OrganizationMemberRole.CEO || authRole == OrganizationMemberRole.Manager) {

            } else {
                return ResponseEntity.badRequest().body("Invalid role");
            }
        }
        OrganizationMember updatedOrganizationMember = organizationMemberService.update(organizationMember);
        return ResponseEntity.ok(updatedOrganizationMember);
    }

    @DeleteMapping("/organization-members/{organization_member_id}")
    public void deleteOrganizationMember(@PathVariable UUID organization_member_id) {
        organizationMemberService.delete(organization_member_id);
    }
}
