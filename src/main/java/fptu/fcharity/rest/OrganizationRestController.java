package fptu.fcharity.rest;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return organizationService.getAll();
    }

    @GetMapping("/organizations/{organization_id}")
    public Organization getOrganizationById(@PathVariable("organization_id") UUID organization_id) {
        return organizationService.getById(organization_id);
    }

    @PostMapping("/organizations")
    public Organization postOrganization(@RequestBody Organization organization) {
        return organizationService.save(organization);
    }

    @PutMapping("/organizations")
    public Organization putOrganization(@RequestBody Organization organization) {
        return organizationService.update(organization);
    }

    @DeleteMapping("/organizations/{organization_id}")
    public void deleteOrganization(@PathVariable UUID organization_id) {
        organizationService.delete(organization_id);
    }
}
