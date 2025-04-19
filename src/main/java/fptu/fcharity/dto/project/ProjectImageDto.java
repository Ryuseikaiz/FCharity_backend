package fptu.fcharity.dto.project;

import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.ProjectImage;
import fptu.fcharity.entity.ProjectMember;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;
@Getter
@Setter
public class ProjectImageDto {
    private UUID id;
    private UUID projectId;
    private String imageUrl;
    private String imageType;
    public ProjectImageDto(ProjectImage pi) {
        this.id =pi.getId();
        this.projectId = pi.getProject().getId();
        this.imageUrl = pi.getImageUrl();
        this.imageType = pi.getImageType();
    }
}
