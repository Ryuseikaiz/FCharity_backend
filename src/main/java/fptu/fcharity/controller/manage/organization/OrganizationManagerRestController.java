package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationUserRoleDTO;
import fptu.fcharity.entity.User;

import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.service.organization.OrganizationManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/organizations/{organizationId}/managers")
public class OrganizationManagerRestController {
    private final OrganizationManagerService organizationManagerService;
    private final UserRepository userRepository;

    @Autowired
    public OrganizationManagerRestController(OrganizationManagerService organizationManagerService, UserRepository userRepository) {
        this.organizationManagerService = organizationManagerService;
        this.userRepository = userRepository;
    }
    @PostMapping
    public ResponseEntity<OrganizationUserRoleDTO> addManager(
            @PathVariable UUID organizationId,
            @RequestBody OrganizationUserRoleDTO organizationUserRoleDTO, Authentication authentication) {
        System.out.println("Add Manager 🤖🤖🤖🤖" + organizationId);

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        UUID currentUserId = user.getUserId();

        OrganizationUserRoleDTO manager = organizationManagerService.addManager(currentUserId, organizationId, organizationUserRoleDTO.getUserId());
        return ResponseEntity.ok(manager);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<OrganizationUserRoleDTO> updateManager(@PathVariable UUID organizationId, @PathVariable UUID userId, @RequestBody OrganizationUserRoleDTO organizationUserRoleDTO, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        UUID currentUserId = user.getUserId();

        OrganizationUserRoleDTO updateManager = organizationManagerService.updateManager(currentUserId, organizationId, userId, organizationUserRoleDTO.getRoleId());
        return ResponseEntity.ok(updateManager);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeManager(@PathVariable UUID organizationId, @PathVariable UUID userId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        UUID currentUserId = user.getUserId();

        organizationManagerService.removeManager(currentUserId, organizationId, userId);
        return ResponseEntity.noContent().build();
    }
}
