package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.IncludesExcludeEventMailAccessDTO;
import fptu.fcharity.dto.organization.OrganizationEventDTO;
import fptu.fcharity.entity.OrganizationEvent;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.service.manage.organization.OrganizationMemberService;
import fptu.fcharity.service.manage.organization.event.OrganizationEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class OrganizationEventRestController {
    private final OrganizationEventService organizationEventService;
    private final UserRepository userRepository;
    private final OrganizationMemberService organizationMemberService;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    public OrganizationEventRestController(OrganizationEventService organizationEventService, UserRepository userRepository, OrganizationMemberService organizationMemberService, OrganizationMemberRepository organizationMemberRepository) {
        this.organizationEventService = organizationEventService;
        this.userRepository = userRepository;
        this.organizationMemberService = organizationMemberService;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    @GetMapping("/organizations/{organizationId}")
    public List<OrganizationEventDTO> getOrganizationEvents(@PathVariable("organizationId") UUID organizationId) {
        System.out.println("OrganizationEventRestController getOrganizationEvents  🍎🍎🍎" + organizationId);
        List<OrganizationEventDTO>  reuslt =  organizationEventService.findByOrganizationId(organizationId);
        System.out.println("getOrganizationEvents" + reuslt);
        return reuslt;
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
        System.out.println("deleting organization event: " + organizationEventId);
        if (organizationEventService.existsByOrganizationEventId(organizationEventId))
        {
            organizationEventService.deleteByOrganizationEventId(organizationEventId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{organizationEventId}/includes-excludes")
    public IncludesExcludeEventMailAccessDTO getIncludesExcludes(@PathVariable UUID organizationEventId) {
        System.out.println("getIncludesExcludes" + organizationEventId);
        IncludesExcludeEventMailAccessDTO result = organizationEventService.getIncludesExcludes(organizationEventId);
        System.out.println("getIncludesExcludes 🚩" + result);
        return result;
    }

    @PostMapping("/includes-excludes")
    public IncludesExcludeEventMailAccessDTO createIncludesExcludes(@RequestBody IncludesExcludeEventMailAccessDTO includesExcludeEventMailAccessDTO) {
        IncludesExcludeEventMailAccessDTO result = organizationEventService.createIncludesExcludes(includesExcludeEventMailAccessDTO);

        List<User> targetUsers = organizationEventService.getTargetUsersForSendingEventInvitationEmail(includesExcludeEventMailAccessDTO);
        OrganizationEventDTO event = organizationEventService.findByOrganizationEventId(includesExcludeEventMailAccessDTO.getOrganizationEventId());

        targetUsers.forEach(targetUser -> {
            organizationEventService.sendEventInvitationEmail(targetUser, event);
        });

        return result;
    }

    @PutMapping("/includes-excludes")
    public IncludesExcludeEventMailAccessDTO updateIncludesExcludes(@RequestBody IncludesExcludeEventMailAccessDTO includesExcludeEventMailAccessDTO) {
        IncludesExcludeEventMailAccessDTO result = organizationEventService.updateIncludesExcludes(includesExcludeEventMailAccessDTO);

        List<User> targetUsers = organizationEventService.getTargetUsersForSendingEventInvitationEmail(includesExcludeEventMailAccessDTO);
        System.out.println("⚓⚓ targetUsers" + targetUsers);
        OrganizationEventDTO event = organizationEventService.findByOrganizationEventId(includesExcludeEventMailAccessDTO.getOrganizationEventId());

        targetUsers.forEach(targetUser -> {
            organizationEventService.sendEventInvitationEmail(targetUser, event);
        });

        System.out.println("🛸🛸 targetUsers" + targetUsers);

        return result;
    }

    @DeleteMapping("/{organizationEventId}/includes-excludes")
    public void deleteIncludesExcludes(@PathVariable UUID organizationEventId) {
        organizationEventService.deleteIncludesExcludes(organizationEventId);
    }
}
