package fptu.fcharity.controller;

import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.service.organization.OrganizationMemberService;
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

    @GetMapping("/organization_members")
    public List<OrganizationMember> getOrganizationMembers() {
        return organizationMemberService.findAll();
    }

    @GetMapping("/organization_members/{organization_member_id}")
    public OrganizationMember getOrganizationMember(@PathVariable UUID organization_member_id) {
        return organizationMemberService.findById(organization_member_id);
    }

    @PostMapping("/organization_members")
    public OrganizationMember createOrganizationMember(@RequestBody OrganizationMember organizationMember) {
        return organizationMemberService.save(organizationMember);
    }

    @PutMapping("/organization_members")
    public OrganizationMember updateOrganizationMember(@RequestBody OrganizationMember organizationMember) {
        return organizationMemberService.save(organizationMember);
    }

    @DeleteMapping("/organization_members/{organization_member_id}")
    public void deleteOrganizationMember(@PathVariable UUID organization_member_id) {
        organizationMemberService.delete(organization_member_id);
    }
}
