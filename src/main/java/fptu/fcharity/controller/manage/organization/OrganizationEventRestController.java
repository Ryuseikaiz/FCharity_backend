package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationEventDTO;
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

    @PostMapping
    public OrganizationEvent addOrganizationEvent(@RequestBody OrganizationEvent organizationEvent) {
        return organizationEventService.save(organizationEvent);
    }

    @PutMapping
    public OrganizationEvent updateOrganizationEvent(@RequestBody OrganizationEvent updatedOrganizationEvent) {
        OrganizationEvent event = organizationEventService
                .findByOrganizationEventId(updatedOrganizationEvent.getOrganizationEventId());

        event.setTitle(updatedOrganizationEvent.getTitle());
        event.setStartTime(updatedOrganizationEvent.getStartTime());
        event.setEndTime(updatedOrganizationEvent.getEndTime());
        event.setBackgroundColor(updatedOrganizationEvent.getBackgroundColor());
        event.setBorderColor(updatedOrganizationEvent.getBorderColor());
        event.setTextColor(updatedOrganizationEvent.getTextColor());
        event.setLocation(updatedOrganizationEvent.getLocation());
        event.setMeetingLink(updatedOrganizationEvent.getMeetingLink());
        event.setEventType(updatedOrganizationEvent.getEventType());
        event.setOrganizer(updatedOrganizationEvent.getOrganizer());
        event.setTargetAudience(updatedOrganizationEvent.getTargetAudience());
        event.setSummary(updatedOrganizationEvent.getSummary());
        event.setFullDescription(updatedOrganizationEvent.getFullDescription());

        return organizationEventService.save(event);
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
