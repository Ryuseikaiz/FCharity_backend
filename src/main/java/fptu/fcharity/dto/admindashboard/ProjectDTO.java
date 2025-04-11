package fptu.fcharity.dto.admindashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ProjectDTO {
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
    private UUID walletAddress;
}
