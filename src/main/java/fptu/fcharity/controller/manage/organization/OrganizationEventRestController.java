package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationEventDTO;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationEvent;
import fptu.fcharity.service.manage.organization.event.OrganizationEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class OrganizationEventRestController {
    private final OrganizationEventService organizationEventService;

    public OrganizationEventRestController(OrganizationEventService organizationEventService) {
        this.organizationEventService = organizationEventService;
    }

    @GetMapping("/organizations/{organizationId}")
    public List<OrganizationEventDTO> getOrganizationEvents(@PathVariable("organizationId") UUID organizationId) {
        System.out.println("OrganizationEventRestController getOrganizationEvents  🍎🍎🍎" + organizationId);
        return organizationEventService.findByOrganizationId(organizationId);
    }

    @PostMapping("/{organizationId}")
    public OrganizationEventDTO addOrganizationEvent(@RequestBody OrganizationEventDTO organizationEventDTO, @PathVariable("organizationId") UUID organizationId) {
        System.out.println("🧊🧊creating organization event: " + organizationEventDTO);
        return organizationEventService.save(organizationEventDTO, organizationId);
    }

    @PutMapping
    public OrganizationEventDTO updateOrganizationEvent(@RequestBody OrganizationEventDTO updatedOrganizationEventDTO) {
        System.out.println("update organization event 🛡️🛡️" + updatedOrganizationEventDTO);
        return organizationEventService.update(updatedOrganizationEventDTO);
    }

    @DeleteMapping("/{organizationEventId}")
    public ResponseEntity<Void> deleteOrganizationEvent(@PathVariable UUID organizationEventId) {
        if (organizationEventService.existsByOrganizationEventId(organizationEventId))
        {
            organizationEventService.deleteByOrganizationEventId(organizationEventId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
