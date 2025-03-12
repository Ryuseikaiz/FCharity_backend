package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDto;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.service.manage.organization.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public List<Organization> getOrganization() {
        return organizationService.getAll();
    }

    @GetMapping("/{organization_id}")
    public Organization getOrganizationById(@PathVariable("organization_id") UUID organization_id) {
        System.out.println("Get organization by id: " + organization_id);
        return organizationService.getById(organization_id);
    }

    @PostMapping("/create")
    public ResponseEntity<?> postOrganization(@RequestBody OrganizationDto organizationDto) {
        System.out.println("creating organization: " + organizationDto);
        Organization o =  organizationService.save(organizationDto);
        return ResponseEntity.ok(o);
    }

    @PutMapping("/update")
    public Organization putOrganization(@RequestBody Organization organization) {
        return organizationService.update(organization);
    }

    @DeleteMapping("/{organization_id}")
    public void deleteOrganization(@PathVariable UUID organization_id) {
        organizationService.delete(organization_id);
    }

}
