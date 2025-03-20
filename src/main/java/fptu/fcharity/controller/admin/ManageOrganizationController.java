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

//    @PutMapping("/approve/{orgId}")
//    public ResponseEntity<String> approveOrganization(@PathVariable UUID orgId) {
//        manageOrganizationService.approveOrganization(orgId);
//        return ResponseEntity.ok("Organization has been approved successfully.");
//    }
    @PutMapping("/unban/{orgId}")
    public ResponseEntity<String> unbanOrganization(@PathVariable UUID orgId) {
        manageOrganizationService.unbanOrganization(orgId);
        return ResponseEntity.ok("Organization has been unbanned successfully.");
    }

    @PutMapping("/ban/{orgId}")
    public ResponseEntity<String> banOrganization(@PathVariable UUID orgId) {
        manageOrganizationService.banOrganization(orgId);
        return ResponseEntity.ok("Organization has been banned successfully.");
    }

    @PutMapping("/activate/{orgId}")
    public ResponseEntity<String> activateOrganization(@PathVariable UUID orgId) {
        manageOrganizationService.activateOrganization(orgId);
        return ResponseEntity.ok("Organization has been activated successfully.");
    }

    @PutMapping("/reject/{orgId}")
    public ResponseEntity<String> rejectOrganization(@PathVariable UUID orgId) {
        manageOrganizationService.rejectOrganization(orgId);
        return ResponseEntity.ok("Organization has been rejected successfully.");
    }
}
