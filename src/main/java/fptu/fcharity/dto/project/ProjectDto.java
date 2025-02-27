package fptu.fcharity.dto.project;

import fptu.fcharity.entity.Category;
import fptu.fcharity.entity.Tag;
import fptu.fcharity.entity.User;
import fptu.fcharity.entity.Wallet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
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

    private UUID tagId;

    private UUID walletId;
}
