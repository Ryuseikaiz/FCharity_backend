package fptu.fcharity.controller.admin;

import fptu.fcharity.dto.admindashboard.OrganizationDTO;
import fptu.fcharity.service.admin.ManageOrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
    @RequestMapping("/api/admin/organizations")
@RequiredArgsConstructor
public class ManageOrganizationController {
    private final ManageOrganizationService manageOrganizationService;

    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        return ResponseEntity.ok(manageOrganizationService.getAllOrganizations());
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable UUID orgId) {
        return ResponseEntity.ok(manageOrganizationService.getOrganizationById(orgId));
    }

    @DeleteMapping("/{orgId}")
    public ResponseEntity<String> deleteOrganization(@PathVariable UUID orgId) {
        manageOrganizationService.deleteOrganization(orgId);
        return ResponseEntity.ok("Organization deleted successfully.");
    }

    @PutMapping("/approve/{orgId}")
    public ResponseEntity<String> approveOrganization(@PathVariable UUID orgId) {
        manageOrganizationService.approveOrganization(orgId);
        return ResponseEntity.ok("Organization has been approved successfully.");
    }

    @PutMapping("/hide/{orgId}")
    public ResponseEntity<String> hideOrganization(@PathVariable UUID orgId) {
        manageOrganizationService.hideOrganization(orgId);
        return ResponseEntity.ok("Organization has been hidden successfully.");
    }

}
