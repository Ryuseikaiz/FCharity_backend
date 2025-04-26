package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organization_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrganizationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "organization_event_id")
    private UUID organizationEventId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "background_color", nullable = false)
    private String backgroundColor;

    @Column(name = "border_color", nullable = false)
    private String borderColor;

    @Column(name = "text_color", nullable = false)
    private String textColor;

    @Column(nullable = false)
    private String location;

    @Column(name = "meeting_link")
    private String meetingLink;


    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", nullable = false)
    private Organization organizer;

    // ['ALL', 'MEMBER', 'MANAGER', 'CEO']
    @Column(name = "target_audience_groups")
    private String targetAudienceGroups;

    @Column(name = "summary" , columnDefinition = "NVARCHAR(MAX)")
    private String summary;

    @Column(name = "full_description",  columnDefinition = "NVARCHAR(MAX)")
    private String fullDescription;

    public enum EventType {
        COMMUNITY_SUPPORT,
        SEMINAR,
        VOLUNTEER,
        FUNDRAISING,
        TRAINING
    }

}
