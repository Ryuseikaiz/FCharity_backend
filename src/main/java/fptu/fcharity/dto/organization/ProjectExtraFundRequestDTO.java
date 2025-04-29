package fptu.fcharity.dto.organization;

import fptu.fcharity.dto.admindashboard.ProjectDTO;
import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.Project;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProjectExtraFundRequestDTO {
    private UUID id;
    private ProjectDto project;
    private BigDecimal amount;
    private String proofImage;
    private String reason;
    private String status;
    private Instant createdDate;
    private Instant updatedDate;
    private OrganizationDTO organization;
}
