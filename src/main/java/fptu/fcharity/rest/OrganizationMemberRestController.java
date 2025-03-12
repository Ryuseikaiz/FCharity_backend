package fptu.fcharity.rest;

import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.service.organization.OrganizationMemberService;
import fptu.fcharity.service.organization.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrganizationMemberRestController {
    private final OrganizationMemberService organizationMemberService;
    private final OrganizationService organizationService;

    @Autowired
    public OrganizationMemberRestController(OrganizationMemberService organizationMemberService, OrganizationService organizationService) {
        this.organizationMemberService = organizationMemberService;
        this.organizationService = organizationService;
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
    public OrganizationMember createOrganizationMember(@RequestBody OrganizationMember organizationMember) {
        return organizationMemberService.save(organizationMember);
    }

    @PutMapping("/organization_members")
    public OrganizationMember updateOrganizationMember(@RequestBody OrganizationMember organizationMember) {
        return organizationMemberService.save(organizationMember);
    }

    @DeleteMapping("/organization-members/{organization_member_id}")
    public void deleteOrganizationMember(@PathVariable UUID organization_member_id) {
        organizationMemberService.delete(organization_member_id);
    }
}
