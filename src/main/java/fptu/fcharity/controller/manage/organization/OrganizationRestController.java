package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDto;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationImage;
import fptu.fcharity.entity.User;

import fptu.fcharity.entity.Wallet;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.service.manage.organization.OrganizationService;
import fptu.fcharity.service.manage.user.UserService;
import fptu.fcharity.service.organization.OrganizationImageService;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrganizationRestController {
    private final OrganizationService organizationService;
    private final UserService userService;

    @Autowired
    public OrganizationRestController(OrganizationService organizationService, UserService userService) {
        this.organizationService = organizationService;
        this.userService = userService;
    }

    @GetMapping("/organizations")
    public List<OrganizationDto> getOrganization() {
        return organizationService.findAll();
    }

    @GetMapping("/organizations/{organization_id}")
    public OrganizationDto getOrganizationById(@PathVariable("organization_id") UUID organization_id) {
        System.out.println("Get organization by id: " + organization_id);
        return organizationService.findById(organization_id);
    }

    @PostMapping("/organizations")  // OK
    public OrganizationDto postOrganization(@RequestBody OrganizationDto organizationDto) throws IOException {
        System.out.println("🤖🤖🤖creating organization: " + organizationDto);
        return organizationService.createOrganization(organizationDto);
    }

    @PutMapping("/organizations")   // OK
    public OrganizationDto putOrganization(@RequestBody OrganizationDto organizationDto) throws IOException {
        return organizationService.updateOrganization(organizationDto);
    }

    @DeleteMapping("/organizations/{organizationId}")
    public void deleteOrganization(@PathVariable UUID organizationId) {
        organizationService.deleteOrganization(organizationId);
    }

    @GetMapping("/organizations/managed")
    public ResponseEntity<List<OrganizationDto>> getManagedOrganizations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser  = userService.findUserByEmail(authentication.getName()); // email

        if (currentUser != null) {
            UUID currentUserId = currentUser.getId();
            List<OrganizationDto> organizations = organizationService.getOrganizationsByCeoOrManager(currentUserId);
            System.out.println("🦔 organizations: " + organizations);
            if (organizations == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(organizations);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/organizations/my-organization/{userId}")
    public ResponseEntity<?> getMyOrganization(@PathVariable UUID userId) {
        Organization organization = organizationService.getMyOrganization(userId);
        return ResponseEntity.ok(organization);
    }
}
