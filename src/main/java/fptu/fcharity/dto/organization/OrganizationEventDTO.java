package fptu.fcharity.dto.organization;

import fptu.fcharity.entity.OrganizationEvent;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
//    private String targetAudience;

    // ['ALL', 'MEMBER', 'MANAGER', 'CEO']
    private String targetAudienceGroups;

    private String summary;
    private String fullDescription;
    private OrganizationDTO organizer;
}
