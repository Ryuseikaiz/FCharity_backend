package fptu.fcharity.controller;

import fptu.fcharity.dto.admindashboard.OrganizationDTO;
import fptu.fcharity.service.ManageOrganizationService;
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

    // Lấy danh sách tổ chức
    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        return ResponseEntity.ok(manageOrganizationService.getAllOrganizations());
    }

    // Lấy tổ chức theo ID
    @GetMapping("/{orgId}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable UUID orgId) {
        return ResponseEntity.ok(manageOrganizationService.getOrganizationById(orgId));
    }

    // Xóa tổ chức
    @DeleteMapping("/{orgId}")
    public ResponseEntity<String> deleteOrganization(@PathVariable UUID orgId) {
        manageOrganizationService.deleteOrganization(orgId);
        return ResponseEntity.ok("Organization deleted successfully.");
    }

    // Duyệt tổ chức từ Pending → Active
    @PutMapping("/{orgId}/approve")
    public ResponseEntity<String> approveOrganization(@PathVariable UUID orgId) {
        manageOrganizationService.approveOrganization(orgId);
        return ResponseEntity.ok("Organization has been approved successfully.");
    }
}
