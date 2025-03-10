package fptu.fcharity.service;

import fptu.fcharity.entity.Project;
import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.*;
import fptu.fcharity.response.project.ProjectFinalResponse;
import fptu.fcharity.utils.constants.ProjectStatus;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.ProjectMapper;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
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
    private final TaggableService taggableService;


    public ProjectService(ProjectMapper projectMapper,
                          ProjectRepository projectRepository,
                          CategoryRepository categoryRepository,
                          TagRepository tagRepository,
                          UserRepository userRepository,
                          WalletRepository walletRepository,
                          OrganizationRepository organizationRepository,
                          TaggableService taggableService) {
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.projectMapper = projectMapper;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.organizationRepository = organizationRepository;
        this.taggableService = taggableService;
    }
    public void takeObject(Project project, ProjectDto projectDto) {
        System.out.println("Category ID: " + projectDto.getCategoryId());
        System.out.println("Category Name: " + projectDto.getLeaderId()); // Nếu lỗi xảy ra ở đây, thì là Lazy Loading

        if (projectDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(projectDto.getCategoryId())
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy Category"));
            System.out.println("Category ID: " + category.getId());
            project.setCategory(category);
        }

        if (projectDto.getLeaderId() != null) {
            User user = userRepository.findById(projectDto.getLeaderId())
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy Leader"));
            project.setLeader(user);
        }

        if (projectDto.getWalletId() != null) {
            Wallet wallet = walletRepository.findById(projectDto.getWalletId())
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy Wallet"));
            project.setWalletAddress(wallet);
        }

        if (projectDto.getOrganizationId() != null) {
            Organization organization = organizationRepository.findById(projectDto.getOrganizationId())
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy Organization"));
            project.setOrganization(organization);
        }
    }

    public ProjectFinalResponse createProject(ProjectDto projectDto) {
        Project project = projectMapper.toEntity( projectDto );
        project.setProjectStatus(ProjectStatus.DONATING);
        takeObject(project, projectDto);
        projectRepository.save(project);
        taggableService.addTaggables(project.getId(), projectDto.getTagIds(), TaggableType.PROJECT);
        return new ProjectFinalResponse(project,taggableService.getTagsOfObject(project.getId(), TaggableType.PROJECT));
    }

    public ProjectFinalResponse getProjectById(UUID id) {
        Project project =  projectRepository.findWithEssentialById(id);
        return new ProjectFinalResponse(project,taggableService.getTagsOfObject(project.getId(), TaggableType.PROJECT));
    }

    public ProjectFinalResponse updateProject(ProjectDto projectDto) {
        Project project = projectRepository.findWithEssentialById(projectDto.getId());
        projectMapper.updateEntityFromDto(projectDto, project);
        takeObject(project, projectDto);
        projectRepository.save(project);
        taggableService.updateTaggables(project.getId(), projectDto.getTagIds(),TaggableType.PROJECT);
        return new ProjectFinalResponse(project,taggableService.getTagsOfObject(project.getId(), TaggableType.PROJECT));
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
