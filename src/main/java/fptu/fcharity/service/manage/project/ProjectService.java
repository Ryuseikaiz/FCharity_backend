package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.SpendingItemDto;
import fptu.fcharity.entity.ProjectConfirmationRequest;
import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.dto.project.ProjectMemberDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.*;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.organization.OrganizationTransactionHistoryRepository;
import fptu.fcharity.repository.manage.project.*;
import fptu.fcharity.repository.manage.request.RequestRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.project.ProjectFinalResponse;
import fptu.fcharity.response.project.ProjectResponse;
import fptu.fcharity.response.project.SpendingItemResponse;
import fptu.fcharity.response.request.RequestFinalResponse;
import fptu.fcharity.service.HelpNotificationService;
import fptu.fcharity.service.ObjectAttachmentService;
import fptu.fcharity.service.TaggableService;
import fptu.fcharity.service.WalletService;
import fptu.fcharity.service.manage.request.RequestService;
import fptu.fcharity.utils.constants.organization.OrganizationTransactionType;
import fptu.fcharity.utils.constants.project.ProjectMemberRole;
import fptu.fcharity.utils.constants.project.ProjectStatus;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.constants.project.TransferRequestStatus;
import fptu.fcharity.utils.constants.request.RequestStatus;
import fptu.fcharity.utils.constants.request.RequestSupportType;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.ProjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
@Service
public class ProjectService {
    private  ProjectRepository projectRepository;
    private OrganizationRepository organizationRepository;
    private CategoryRepository categoryRepository;
    private ProjectMapper projectMapper;
    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private WalletService walletService;
    private TaggableService taggableService;
    private ProjectImageService projectImageService;
    private RequestRepository requestRepository;
    private ProjectMemberService projectMemberService;
    private ProjectMemberRepository projectMemberRepository;
    private ProjectConfirmationRequestRepository projectConfirmationRequestRepository;
    private SpendingItemService spendingItemService;
    private SpendingPlanRepository spendingPlanRepository;
    private SpendingItemRepository spendingItemRepository;
    private SpendingDetailRepository spendingDetailRepository;
    private OrganizationTransactionHistoryRepository organizationTransactionHistoryRepository;

    private HelpNotificationService notificationService;
    private TransferRequestRepository transferRequestRepository;

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
                          ProjectImageService projectImageService,
                          ProjectMemberService projectMemberService,
                          ProjectConfirmationRequestRepository projectConfirmationRequestRepository,
                          SpendingItemService spendingItemService,
                          SpendingPlanRepository spendingPlanRepository,
                          SpendingItemRepository spendingItemRepository,
                          SpendingDetailRepository spendingDetailRepository,
                          TransferRequestRepository transferRequestRepository,
                          OrganizationTransactionHistoryRepository organizationTransactionHistoryRepository,
        HelpNotificationService notificationService ) {
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
        this.projectConfirmationRequestRepository = projectConfirmationRequestRepository;
        this.spendingItemService = spendingItemService;
        this.spendingPlanRepository = spendingPlanRepository;
        this.spendingItemRepository = spendingItemRepository;
        this.spendingDetailRepository = spendingDetailRepository;
        this.transferRequestRepository = transferRequestRepository;
        this.organizationTransactionHistoryRepository = organizationTransactionHistoryRepository;
        this.notificationService = notificationService;
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
        User requestOwner = project.getRequest().getUser();
        notificationService.notifyUser(
                requestOwner,
                "Project created",
                null,
                "Project \"" + project.getProjectName() + "\" has been registered \"",
                "/requests/" + project.getRequest().getId()
        );

        User leader = project.getLeader();
        notificationService.notifyUser(
                leader,
                "Project created",
                null,
                "Ban duoc moi lam leader cua project: \"" + project.getProjectName(),
                "/projects/" + project.getId()
        );

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

    public List<ProjectFinalResponse> getProjectByOrgId(UUID orgId) {
        List<Project> projects = projectRepository.findByOrganizationOrganizationId(orgId);
        return projects.stream().map(project -> new ProjectFinalResponse(new ProjectResponse(project),
                taggableService.getTagsOfObject(project.getId(), TaggableType.PROJECT),
                projectImageService.getProjectImages(project.getId()))
        ).toList();
    }

    public void handleActiveProjectJob(UUID id) {
        Project p = projectRepository.findWithEssentialById(id);
        SpendingPlan plan = spendingPlanRepository.findByProjectId(p.getId());
        BigDecimal totalDonations = p.getWalletAddress().getBalance();
        //if donations < plan.getEstimatedTotalCost()
        if(totalDonations.compareTo(plan.getEstimatedTotalCost()) < 0){
            //send confirm
            transferRequestRepository.save(new TransferRequest(
                    p.getRequest(),
                    p,
                    totalDonations,
                    "The project has reached its start time, but the donations are still insufficient. We will return all the donated funds to you so that you may use them for other purposes. "+
                            "Please fill the form below to continue the receive money process.","",
                    TransferRequestStatus.PENDING_USER_CONFIRM
            ));
            p.setProjectStatus(ProjectStatus.PROCESSING);
            p.setActualStartTime(Instant.now());
            projectRepository.save(p);
            notificationService.notifyUser(
                    p.getRequest().getUser(),
                    "Transfer request to your request: " + p.getRequest().getTitle(),
                    null,
                    "There is a transfer request from project who help your request: " + p.getProjectName(),
                    "/user/manage-profile/myrequests"
            );
        }else{
            if(p.getRequest().getSupportType() == RequestSupportType.MONEY) {
                //send confirm
                transferRequestRepository.save(new TransferRequest(
                        p.getRequest(),
                        p,
                        totalDonations,
                        "The project has reached its start time, and the donations are sufficient. We will send the donated funds to you. Please fill the form below to continue the receive money process.", "",
                        TransferRequestStatus.PENDING_USER_CONFIRM
                ));
                p.setProjectStatus(ProjectStatus.PROCESSING);
                p.setActualStartTime(Instant.now());
                projectRepository.save(p);
                notificationService.notifyUser(
                        p.getRequest().getUser(),
                        "Transfer request to your request: " + p.getRequest().getTitle(),
                        null,
                        "There is a transfer request from project who help your request: " + p.getProjectName(),
                        "/user/manage-profile/myrequests"
                );
            }else{
                BigDecimal totalCost = spendingItemRepository.findBySpendingPlanId(plan.getId())
                        .stream()
                        .map(SpendingItem::getEstimatedCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                SpendingItem item = new SpendingItem();
                item.setItemName("Extra funds");
                item.setEstimatedCost(totalDonations.subtract(totalCost));
                item.setSpendingPlan(plan);
                item.setNote("Extra funds for project");
                item.setSpendingPlan(plan);
                item.setCreatedDate(Instant.now());
                spendingItemRepository.save(item);

                SpendingDetail spendingDetail = new SpendingDetail();
                spendingDetail.setSpendingItem(item);
                spendingDetail.setAmount(item.getEstimatedCost());
                spendingDetail.setDescription("Extra funds for project");
                spendingDetail.setTransactionTime(Instant.now());
                spendingDetailRepository.save(spendingDetail);

                p.getWalletAddress().setBalance(
                        p.getWalletAddress().getBalance().subtract(spendingDetail.getAmount())
                );
                walletRepository.save(p.getWalletAddress());

                //send extra cost to org
                OrganizationTransactionHistory organizationTransactionHistory = new OrganizationTransactionHistory();
                organizationTransactionHistory.setOrganization(p.getOrganization());
                organizationTransactionHistory.setTransactionTime(Instant.now());
                organizationTransactionHistory.setTransactionType(OrganizationTransactionType.EXTRACT_EXTRA_COST);
                organizationTransactionHistory.setAmount(spendingDetail.getAmount());
                organizationTransactionHistory.setProject(p);
                organizationTransactionHistory.setMessage("Extracted extra funds from project");
                organizationTransactionHistoryRepository.save(organizationTransactionHistory);

                p.getOrganization().getWalletAddress().setBalance(
                        p.getOrganization().getWalletAddress().getBalance().add(spendingDetail.getAmount())
                );
                walletRepository.save(p.getOrganization().getWalletAddress());
            }
            //make spending detail for project

        }
    }
}
