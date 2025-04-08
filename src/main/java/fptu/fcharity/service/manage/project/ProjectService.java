package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.dto.project.ProjectMemberDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.*;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.project.ProjectImageRepository;
import fptu.fcharity.repository.manage.project.ProjectMemberRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.request.RequestRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.project.ProjectFinalResponse;
import fptu.fcharity.response.project.ProjectResponse;
import fptu.fcharity.response.request.RequestFinalResponse;
import fptu.fcharity.service.ObjectAttachmentService;
import fptu.fcharity.service.TaggableService;
import fptu.fcharity.service.WalletService;
import fptu.fcharity.service.manage.request.RequestService;
import fptu.fcharity.utils.constants.project.ProjectMemberRole;
import fptu.fcharity.utils.constants.project.ProjectStatus;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.constants.request.RequestStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.ProjectMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final OrganizationRepository organizationRepository;
    private final TaggableService taggableService;
    private final ProjectImageService projectImageService;
    private final RequestRepository requestRepository;
    private final ProjectMemberService projectMemberService;
    private final ProjectMemberRepository projectMemberRepository;
    public ProjectService(ProjectMapper projectMapper,
                          ProjectRepository projectRepository,
                          CategoryRepository categoryRepository,
                          UserRepository userRepository,
                          WalletRepository walletRepository,
                          OrganizationRepository organizationRepository,
                          TaggableService taggableService,
                          RequestService requestService,
                          WalletService walletService,
                          RequestRepository requestRepository,
                          ProjectMemberRepository projectMemberRepository,
                          ProjectImageService projectImageService, ProjectMemberService projectMemberService) {
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.projectMapper = projectMapper;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.organizationRepository = organizationRepository;
        this.taggableService = taggableService;
        this.projectImageService = projectImageService;
        this.walletService = walletService;
        this.requestRepository = requestRepository;
        this.projectMemberService = projectMemberService;
        this.projectMemberRepository = projectMemberRepository;
    }
    public List<ProjectFinalResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAllWithInclude();
        return projects.stream().map(project -> new ProjectFinalResponse(new ProjectResponse(project),
                taggableService.getTagsOfObject(project.getId(),TaggableType.PROJECT),
                projectImageService.getProjectImages(project.getId()))
        ).toList();
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
        if (projectDto.getRequestId() != null) {
            HelpRequest r = requestRepository.findWithIncludeById(projectDto.getRequestId());
            project.setRequest(r);
        }
    }
    public ProjectFinalResponse createProject(ProjectDto projectDto) {
        Wallet newWallet = walletService.save();
        Project project = projectMapper.toEntity( projectDto );
        project.setProjectStatus(ProjectStatus.PLANNING);
        project.setWalletAddress(newWallet);
        takeObject(project, projectDto);
        // Set status of request to REGISTERED
        project.getRequest().setStatus(RequestStatus.REGISTERED);
        requestRepository.save(project.getRequest());
        //set user to leader
        User u = userRepository.findWithEssentialById(project.getLeader().getId());
        u.setCreatedDate(Instant.now());
        u.setUserRole(User.UserRole.Leader);
        userRepository.save(u);

        //save project
        project.setCreatedAt(Instant.now());
        projectRepository.save(project);

        ProjectMemberDto projectMemberDto = new ProjectMemberDto(project.getLeader().getId(),project.getId(),ProjectMemberRole.LEADER);
        projectMemberService.addProjectMember(projectMemberDto);
        taggableService.addTaggables(project.getId(), projectDto.getTagIds(), TaggableType.PROJECT);
        projectImageService.saveProjectImages(project.getId(), projectDto.getImageUrls());
        projectImageService.saveProjectImages(project.getId(), projectDto.getVideoUrls());
        return new ProjectFinalResponse(new ProjectResponse(project),
                taggableService.getTagsOfObject(project.getId(), TaggableType.PROJECT),
                projectImageService.getProjectImages(project.getId()));
    }

    public ProjectFinalResponse getProjectById(UUID id) {
        Project project =  projectRepository.findWithEssentialById(id);
        if(project == null ){
            throw new ApiRequestException("Project not found");
        }
        return new ProjectFinalResponse(new ProjectResponse(project),
                taggableService.getTagsOfObject(project.getId(), TaggableType.PROJECT),
                projectImageService.getProjectImages(project.getId()));
    }

    public ProjectFinalResponse updateProject(ProjectDto projectDto) {
        Project project = projectRepository.findWithEssentialById(projectDto.getId());
        projectMapper.updateEntityFromDto(projectDto, project);
        project.setUpdatedAt(Instant.now());
        takeObject(project, projectDto);
        if (projectDto.getTagIds() != null) {
            taggableService.updateTaggables(project.getId(), projectDto.getTagIds(),TaggableType.PROJECT);
        } else {
            taggableService.updateTaggables(project.getId(), new ArrayList<>(),TaggableType.PROJECT);
        }
        projectImageService.clearProjectImages(project.getId());
        projectImageService.saveProjectImages(project.getId(), projectDto.getImageUrls());
        projectImageService.saveProjectImages(project.getId(), projectDto.getVideoUrls());
        projectRepository.save(project);
        return new ProjectFinalResponse(new ProjectResponse(project),
                taggableService.getTagsOfObject(project.getId(), TaggableType.PROJECT),
                projectImageService.getProjectImages(project.getId()));
    }

    public void deleteProject(UUID projectId) {
        try
        {
            projectImageService.clearProjectImages(projectId);
            projectRepository.deleteById(projectId);
        }
        catch (Exception e)
        {
            throw new ApiRequestException("Error: "+ e.getMessage());
        }
    }

    public List<ProjectFinalResponse> getMyProject(UUID userId) {
        List<ProjectMember> projectMemberList =  projectMemberRepository.findMyProjectMembers(userId);
        List<Project> projects = projectMemberList.stream().map(
                projectMember -> projectRepository.findWithEssentialById(projectMember.getProject().getId())
        ).toList();
        System.out.println(projects.getFirst().getWalletAddress().getId());
        List<ProjectFinalResponse> pResponse = projects.stream().map(project -> new ProjectFinalResponse(new ProjectResponse(project),
                taggableService.getTagsOfObject(project.getId(), TaggableType.PROJECT),
                projectImageService.getProjectImages(project.getId()))
        ).toList();
        return pResponse;
    }

    public ProjectFinalResponse getProjectByWalletId(UUID walletId) {
        Project project = projectRepository.findByWalletAddressId(walletId);
        return new ProjectFinalResponse(new ProjectResponse(project),
                taggableService.getTagsOfObject(project.getId(), TaggableType.PROJECT),
                projectImageService.getProjectImages(project.getId()));
    }
}
