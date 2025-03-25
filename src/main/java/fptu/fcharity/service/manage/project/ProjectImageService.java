package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.ProjectImageDto;
import fptu.fcharity.entity.ObjectAttachment;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.ProjectImage;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.project.ProjectImageRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.utils.constants.project.ProjectImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectImageService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectImageRepository projectImageRepository;
    public void takeObject(ProjectImage prI, UUID projectId) {
        if (projectId!= null) {
            Project p = projectRepository.findWithEssentialById(projectId);
            prI.setProject(p);
        }
    }

    public void saveProjectImages(UUID projectId, List<String> urls) {
        List<ProjectImage> pis = new ArrayList<>();
        for (String url : urls) {
            ProjectImage attachment = new ProjectImage();
            attachment.setImageUrl(url);
            attachment.setImageType(ProjectImageType.GENERAL);
            takeObject(attachment, projectId);
            pis.add(attachment);
        }
        projectImageRepository.saveAll(pis);
    }

    public List<ProjectImageDto> getProjectImages(UUID projectId) {
        return projectImageRepository.findByProjectId(projectId).stream().map(ProjectImageDto::new).toList();
    }
    public void clearProjectImages(UUID projectId) {
        projectImageRepository.deleteByProjectId(projectId);
    }
}
