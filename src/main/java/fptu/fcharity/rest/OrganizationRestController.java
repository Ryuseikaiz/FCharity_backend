package fptu.fcharity.rest;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.service.organization.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrganizationRestController {
    private final OrganizationService organizationService;

    @Autowired
    public OrganizationRestController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/organizations")
    public List<Organization> getOrganization() {
        return organizationService.getAllOrganizations();
    }

    @GetMapping("/organizations/{organization_id}")
    public Organization getOrganizationById(@PathVariable("organization_id") UUID organization_id) {
        System.out.println("Get organization by id: " + organization_id);
        return organizationService.getById(organization_id);
    }

    @PostMapping("/organizations")
    public Organization postOrganization(@RequestBody Organization organization) throws IOException {
        System.out.println("creating organization: " + organization);
        return organizationService.createOrganization(organization);
    }

    @PutMapping("/organizations")
    public Organization putOrganization(@RequestBody Organization organization) throws IOException {
        return organizationService.updateOrganization(organization);
    }

    @DeleteMapping("/organizations/{organization_id}")
    public void deleteOrganization(@PathVariable UUID organization_id) {
        organizationService.deleteOrganization(organization_id);
    }
}
