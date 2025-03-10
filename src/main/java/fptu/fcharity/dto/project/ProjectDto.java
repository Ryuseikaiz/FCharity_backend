package fptu.fcharity.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class ProjectDto {
    private UUID id;
    private String projectName;
    private UUID organizationId;
    private UUID leaderId;
    private String email;

    private String phoneNumber;

    private String projectDescription;

    private String projectStatus;

    private String reportFile;

    private Instant plannedStartTime;

    private Instant plannedEndTime;

    private Instant actualStartTime;

    private Instant actualEndTime;

    private String shutdownReason;

    private UUID categoryId;

    private UUID walletId;

    private List<UUID> tagIds;

    private List<String> images;

    private List<String> videos;
}
