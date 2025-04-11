package fptu.fcharity.response.project;

import fptu.fcharity.dto.project.ProjectImageDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.Taggable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class ProjectFinalResponse {
    private ProjectResponse project;
    private List<Taggable> projectTags;
    private List<ProjectImageDto> attachments;
}
