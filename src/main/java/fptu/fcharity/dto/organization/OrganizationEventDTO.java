package fptu.fcharity.dto.organization;

import fptu.fcharity.entity.OrganizationEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationEventDTO {
    private UUID organizationEventId;
    private String title;
    private Instant startTime;
    private Instant endTime;
    private String backgroundColor;
    private String borderColor;
    private String textColor;
    private String location;
    private String meetingLink;
    private OrganizationEvent.EventType eventType;
    private String targetAudience;
    private String summary;
    private String fullDescription;
    private OrganizationDTO organizer;
}
