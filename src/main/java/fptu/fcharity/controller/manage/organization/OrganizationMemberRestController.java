package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationMemberDTO;
import fptu.fcharity.dto.organization.UserDTO;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.service.manage.organization.OrganizationMemberService;
import fptu.fcharity.service.manage.organization.OrganizationService;
import fptu.fcharity.service.manage.user.UserService;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OrganizationMemberRestController {
    private final OrganizationMemberService organizationMemberService;
    private final OrganizationService organizationService;
    private final UserService userService;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    public OrganizationMemberRestController(OrganizationMemberService organizationMemberService, OrganizationService organizationService, UserService userService, OrganizationMemberRepository organizationMemberRepository) {
        this.organizationMemberService = organizationMemberService;
        this.organizationService = organizationService;
        this.userService = userService;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    @GetMapping("/organization-members/{organizationId}/users-not-in-organization")
    public List<UserDTO> getUser(@PathVariable UUID organizationId) {
        return organizationMemberService.getAllUsersNotInOrganization(organizationId);
    }

    // chưa dùng (thống kê)
    @GetMapping("/organization-members")
    public List<OrganizationMember> getAllMembers() {
        return organizationMemberService.findAll();
    }

    // Lấy danh sách các thành viên trong tổ chức ở tất cả vai trò
    @GetMapping("/organization-members/{organizationId}")
    public List<OrganizationMemberDTO> getAllMembersInOrganization(@PathVariable UUID organizationId) {
        return  organizationMemberService.findOrganizationMemberByOrganizationId(organizationId);
    }

    // Tạo thành viên mới cho một tổ chức
    @PostMapping("/organization-members/{organizationId}/{userId}")
    public OrganizationMember createOrganizationMember(@PathVariable UUID organizationId, @PathVariable UUID userId) {
        OrganizationMember organizationMember = new OrganizationMember();
        organizationMember.setOrganization(organizationService.findEntityById(organizationId));
        organizationMember.setUser(userService.getById(userId).orElseThrow(() -> new ApiRequestException("User not found")));
        System.out.println(organizationMember);

        return organizationMemberService.save(organizationMember);
    }

    // Cập nhật thông tin thành viên trong tổ chức
    @PutMapping("/organization-members")
    public ResponseEntity<?> updateOrganizationMember(@RequestBody OrganizationMember organizationMember, Authentication authentication) {
        OrganizationMember currentOrganizationMemberInfo = organizationMemberService.findById(organizationMember.getMembershipId()).orElseThrow(()-> new ApiRequestException("Member not found"));
        User authUser  = userService.findUserByEmail(authentication.getName());

        if (!Objects.equals(organizationMember.getMemberRole(), currentOrganizationMemberInfo.getMemberRole())) {
            OrganizationMemberRole authRole = organizationMemberService.findUserRoleInOrganization(authUser.getId(), organizationMember.getOrganization().getOrganizationId());
            if (authRole == OrganizationMemberRole.CEO ) {
                return ResponseEntity.ok(organizationMemberService.update(organizationMember));
            }
            else if (authRole == OrganizationMemberRole.MANAGER) {
                OrganizationMember member = organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(organizationMember.getUser().getId(), organizationMember.getOrganization().getOrganizationId());
                if (member.getMemberRole() == OrganizationMemberRole.CEO || member.getMemberRole() == OrganizationMemberRole.MANAGER) {
                    return ResponseEntity.badRequest().body("You are not allowed to changed Ceo or Manager role. (Only Ceo is allowed!)");
                } else
                    return ResponseEntity.ok(organizationMemberService.update(organizationMember));
            } else {
                return ResponseEntity.badRequest().body("Invalid role");
            }
        }
        return ResponseEntity.ok(organizationMemberService.update(organizationMember));
    }

    // Xóa thành viên khỏi tổ chức
    @DeleteMapping("/organization-members/{organizationMemberId}")
    public UUID deleteOrganizationMember(@PathVariable UUID organizationMemberId) {
        organizationMemberService.delete(organizationMemberId);
        return organizationMemberId;
    }
}
