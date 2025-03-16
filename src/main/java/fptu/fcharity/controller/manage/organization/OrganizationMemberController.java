package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.service.manage.organization.OrganizationMemberService;
import fptu.fcharity.service.manage.organization.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/organizations/members")
public class OrganizationMemberController {
    //viết cụ thể các endpoint là cho chức năng gì
    //thêm invite thành viên, review các yêu cầu tham gia
    //test lại với db mới

    private final OrganizationMemberService organizationMemberService;

    public OrganizationMemberController(OrganizationMemberService organizationMemberService) {
        this.organizationMemberService = organizationMemberService;
    }

    @GetMapping()
    public List<OrganizationMember> getOrganizationMembers() {
        return organizationMemberService.findAll();
    }

    @GetMapping("/{organization_id}")
    public Optional<OrganizationMember> getOrganizationMember(@PathVariable UUID organization_id) {
        return organizationMemberService.findById(organization_id);
    }

    @PostMapping
    public OrganizationMember createOrganizationMember(@RequestBody OrganizationMember organizationMember) {
        return organizationMemberService.save(organizationMember);
    }

    @PutMapping
    public OrganizationMember updateOrganizationMember(@RequestBody OrganizationMember organizationMember) {
        return organizationMemberService.save(organizationMember);
    }

    @DeleteMapping("/{organization_member_id}")
    public void deleteOrganizationMember(@PathVariable UUID organization_member_id) {
        organizationMemberService.delete(organization_member_id);
    }
}
