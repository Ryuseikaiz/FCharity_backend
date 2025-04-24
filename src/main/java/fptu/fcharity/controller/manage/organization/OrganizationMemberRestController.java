package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.UserDTO;
import fptu.fcharity.dto.organization.OrganizationMemberDTO;
import fptu.fcharity.service.manage.organization.OrganizationMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrganizationMemberRestController {
    private final OrganizationMemberService organizationMemberService;
    @Autowired
    public OrganizationMemberRestController(OrganizationMemberService organizationMemberService) {
        this.organizationMemberService = organizationMemberService;
    }

    @GetMapping("/organization-members/{organizationId}/users-not-in-organization")
    public List<UserDTO> getUser(@PathVariable UUID organizationId) {
        return organizationMemberService.getAllUsersNotInOrganization(organizationId);
    }

    // chưa dùng (thống kê)
    @GetMapping("/organization-members")
    public List<OrganizationMemberDTO> getAllMembers() {
        return organizationMemberService.findAll();
    }

    // Lấy danh sách các thành viên trong tổ chức ở tất cả vai trò
    @GetMapping("/organization-members/{organizationId}")
    public List<OrganizationMemberDTO> getAllMembersInOrganization(@PathVariable UUID organizationId) {
        return  organizationMemberService.findOrganizationMemberByOrganizationId(organizationId);
    }

    // Tạo thành viên mới cho một tổ chức
    @PostMapping("/organization-members/{organizationId}/{userId}")
    public OrganizationMemberDTO createOrganizationMember(@PathVariable UUID organizationId, @PathVariable UUID userId) {
        return organizationMemberService.createOrganizationMember(organizationId, userId);
    }

    // Cập nhật thông tin thành viên trong tổ chức
    @PutMapping("/organization-members/update-role")
    public OrganizationMemberDTO updateOrganizationMember(@RequestBody OrganizationMemberDTO organizationMemberDTO) {

        return organizationMemberService.updateRole(organizationMemberDTO);
    }

    // Xóa thành viên khỏi tổ chức  - trả về membershipId nếu thành công
    @DeleteMapping("/organization-members/{membershipId}")
    public void deleteOrganizationMember(@PathVariable UUID membershipId) {
        organizationMemberService.delete(membershipId);
    }
}
