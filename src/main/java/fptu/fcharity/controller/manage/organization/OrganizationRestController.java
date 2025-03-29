package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDto;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.User;

import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.service.manage.organization.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrganizationRestController {
    private final OrganizationService organizationService;
    private final UserRepository userRepository;

    @Autowired
    public OrganizationRestController(OrganizationService organizationService, UserRepository userRepository) {
        this.organizationService = organizationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/organizations")
    public List<Organization> getOrganization() {
        return organizationService.getAllOrganizations();
    }

    @GetMapping("/organizations/{organizationId}")
    public Organization getOrganizationById(@PathVariable("organizationId") UUID organizationId) {
        System.out.println("Get organization by id: " + organizationId);
        return organizationService.getById(organizationId);
    }

    @PostMapping("/organizations")
    public Organization postOrganization(@RequestBody Organization organization, Authentication authentication) throws IOException {
        System.out.println("creating organization: " + organization);
        User ceo = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("Anonymous user are not allowed to create organization"));
        organization.setCeo(ceo);
        return organizationService.createOrganization(organization);
    }

    @PutMapping("/organizations")
    public Organization putOrganization(@RequestBody Organization organization) throws IOException {
        return organizationService.updateOrganization(organization);
    }

    @DeleteMapping("/organizations/{organizationId}")
    public void deleteOrganization(@PathVariable UUID organizationId) {
        organizationService.deleteOrganization(organizationId);
    }

    @GetMapping("/organizations/managed")
    public ResponseEntity<List<OrganizationDto>> getManagedOrganizations(Authentication authentication) {
        Optional<User> currentUser  = userRepository.findByEmail(authentication.getName()); // email

        if (currentUser.isPresent()) {
            UUID currentUserId = currentUser.get().getId();
            List<OrganizationDto> organizations = organizationService.getOrganizationsByManager(currentUserId);
            System.out.println("🦔 organizations: " + organizations);
            if (organizations == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(organizations);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
