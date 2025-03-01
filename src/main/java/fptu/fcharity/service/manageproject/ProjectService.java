package fptu.fcharity.service.manageproject;

import fptu.fcharity.entity.Project;
import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.*;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.ProjectMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final OrganizationRepository organizationRepository;


    public ProjectService(ProjectMapper projectMapper,
                          ProjectRepository projectRepository,
                          CategoryRepository categoryRepository,
                          TagRepository tagRepository,
                          UserRepository userRepository,
                          WalletRepository walletRepository,
                          OrganizationRepository organizationRepository) {
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.projectMapper = projectMapper;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.organizationRepository = organizationRepository;
    }

    public void takeObject(Project project, ProjectDto projectDto) {
        if (projectDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(projectDto.getCategoryId()).orElse(null);
            project.setCategory(category);
        }

        if (projectDto.getLeaderId() != null) {
            User user = userRepository.findById(projectDto.getLeaderId()).orElse(null);
            project.setLeader(user);
        }
        if (projectDto.getWalletId() != null) {
            Wallet wallet = walletRepository.findById(projectDto.getWalletId()).orElse(null);
            project.setWalletAddress(wallet);
        }
        if (projectDto.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(projectDto.getOrganizationId()).orElse(null);
            project.setOrganization(organization);
        }
    }

    public Project createProject(ProjectDto projectDto) {
        Project project = projectMapper.toEntity( projectDto );
        takeObject(project, projectDto);
        return projectRepository.save(project);
    }

    public Project getProjectById(UUID id) {
        return projectRepository.findWithCategoryTagWalletById(id);
    }

    public Project updateProject(ProjectDto projectDto) {
        Project project = projectRepository.findById(projectDto.getId()).get();
        projectMapper.updateEntityFromDto(projectDto, project);
        takeObject(project, projectDto);
        return projectRepository.save(project);
    }
    public void deleteProject(UUID projectId) {
        try
        {
            projectRepository.deleteById(projectId);
        }
        catch (Exception e)
        {
            throw new ApiRequestException("Error: "+ e.getMessage());
        }
    }
}
