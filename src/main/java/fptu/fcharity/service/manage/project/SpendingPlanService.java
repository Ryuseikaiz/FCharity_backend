package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.SpendingPlanDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.SpendingItemRepository;
import fptu.fcharity.repository.manage.project.SpendingPlanRepository;
import fptu.fcharity.response.project.SpendingPlanResponse;
import fptu.fcharity.service.HelpNotificationService;
import fptu.fcharity.utils.constants.project.ProjectStatus;
import fptu.fcharity.utils.constants.project.SpendingPlanStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SpendingPlanService {
    @Autowired
    private SpendingPlanRepository spendingPlanRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SpendingItemRepository spendingItemRepository;
    @Autowired
    private HelpNotificationService notificationService;

    public SpendingPlanResponse createSpendingPlan(SpendingPlanDto dto) {
        Project project = projectRepository.findWithEssentialById(dto.getProjectId());
        SpendingPlan plan = new SpendingPlan();
        plan.setProject(project);
        plan.setPlanName(dto.getPlanName());
        plan.setDescription(dto.getDescription());
        plan.setMaxExtraCostPercentage(dto.getMaxExtraCostPercentage());
        plan.setEstimatedTotalCost(dto.getEstimatedTotalCost());
        plan.setApprovalStatus(SpendingPlanStatus.PREPARING);
        plan.setCreatedDate(Instant.now());

        User ceo = project.getOrganization().getCeo();// hoặc tương tự tuỳ theo model của bạn
        notificationService.notifyUser(
                ceo,
                null,
                "New spending plan created",
                "A new spending plan has been submitted for approval in project: " + project.getProjectName(),
                "/admin/spending-plan/" + plan.getId()
        );

        return toResponse(spendingPlanRepository.save(plan));
    }

    public SpendingPlanResponse getSpendingPlanById(UUID id) {
        SpendingPlan plan = spendingPlanRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Spending plan not found"));
        return toResponse(plan);
    }

    public SpendingPlanResponse updateSpendingPlan(UUID id, SpendingPlanDto dto) {
        SpendingPlan plan = spendingPlanRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Spending plan not found"));

        if (dto.getPlanName() != null) plan.setPlanName(dto.getPlanName());
        if (dto.getDescription() != null) plan.setDescription(dto.getDescription());
        if (dto.getEstimatedTotalCost() != null) plan.setEstimatedTotalCost(dto.getEstimatedTotalCost());
        if (dto.getMaxExtraCostPercentage() != null) plan.setMaxExtraCostPercentage(dto.getMaxExtraCostPercentage());
        if (dto.getApprovalStatus() != null) plan.setApprovalStatus(dto.getApprovalStatus());

        plan.setUpdatedDate(Instant.now());

        return toResponse(spendingPlanRepository.save(plan));
    }

    public void deleteSpendingPlan(UUID id) {
        SpendingPlan plan = spendingPlanRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Spending plan not found"));
        spendingPlanRepository.delete(plan);
    }

    private SpendingPlanResponse toResponse(SpendingPlan plan) {
        SpendingPlanResponse res = new SpendingPlanResponse();
        res.setId(plan.getId());
        res.setProjectId(plan.getProject().getId());
        res.setPlanName(plan.getPlanName());
        res.setDescription(plan.getDescription());
        res.setEstimatedTotalCost(plan.getEstimatedTotalCost());
        res.setMaxExtraCostPercentage(plan.getMaxExtraCostPercentage());
        res.setApprovalStatus(plan.getApprovalStatus());
        res.setCreatedDate(plan.getCreatedDate());
        res.setUpdatedDate(plan.getUpdatedDate());
        return res;
    }

    public SpendingPlanResponse getSpendingPlanByProjectId(UUID projectId) {
        SpendingPlan p= spendingPlanRepository.findByProjectId(projectId);
        return  toResponse(p); // Return the first plan or handle as needed
    }
    public SpendingPlanResponse approvePlan(UUID id,UUID projectId){
        SpendingPlan plan = spendingPlanRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Spending plan not found"));
        Project project = projectRepository.findWithEssentialById(projectId);
        if (plan.getProject().getId() != project.getId()){
            throw new ApiRequestException("Spending plan not found");
        }
        BigDecimal totalCost = spendingItemRepository.findBySpendingPlanId(id)
                .stream()
                .map(SpendingItem::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal extraCost = totalCost.multiply(new BigDecimal("0.1"));
        project.setProjectStatus(ProjectStatus.DONATING);
        projectRepository.save(project);
        plan.setEstimatedTotalCost(totalCost.add(extraCost));
        plan.setApprovalStatus(SpendingPlanStatus.APPROVED);

        User leader = project.getLeader();
        notificationService.notifyUser(
                leader,
                null,
                "Spending plan approved",
                "The spending plan for project '" + project.getProjectName() + "' has been approved.",
                "/admin/spending-plan/"
        );
        return toResponse(spendingPlanRepository.save(plan));
    }

    public SpendingPlanResponse saveFromExcel(SpendingPlan spendingPlan, UUID projectId) {
        Project project = projectRepository.findWithEssentialById(projectId);
        spendingPlan.setProject(project);
        spendingPlan.setApprovalStatus(SpendingPlanStatus.PREPARING);
        spendingPlan.setCreatedDate(Instant.now());
        spendingPlanRepository.save(spendingPlan);
        return toResponse(spendingPlan);
    }
}
