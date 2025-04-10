package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.SpendingPlanDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.SpendingItem;
import fptu.fcharity.entity.SpendingPlan;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.SpendingItemRepository;
import fptu.fcharity.repository.manage.project.SpendingPlanRepository;
import fptu.fcharity.response.project.SpendingPlanResponse;
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

    public SpendingPlanResponse createSpendingPlan(SpendingPlanDto dto) {
        Project project = projectRepository.findWithEssentialById(dto.getProjectId());
        SpendingPlan plan = new SpendingPlan();
        plan.setProject(project);
        plan.setPlanName(dto.getPlanName());
        plan.setDescription(dto.getDescription());
        plan.setMinRequiredDonationAmount(dto.getMinRequiredDonationAmount());
        plan.setEstimatedTotalCost(dto.getEstimatedTotalCost());
        plan.setApprovalStatus(SpendingPlanStatus.PREPARING);
        plan.setCreatedDate(Instant.now());

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
        if (dto.getMinRequiredDonationAmount() != null) plan.setMinRequiredDonationAmount(dto.getMinRequiredDonationAmount());
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
        res.setMinRequiredDonationAmount(plan.getMinRequiredDonationAmount());
        res.setApprovalStatus(plan.getApprovalStatus());
        res.setCreatedDate(plan.getCreatedDate());
        res.setUpdatedDate(plan.getUpdatedDate());
        return res;
    }

    public List<SpendingPlanResponse> getAllSpendingPlansByProject(UUID projectId) {
        return spendingPlanRepository.findByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SpendingPlanResponse> getSpendingPlanByProjectId(UUID projectId) {
        List<SpendingPlan> plans = spendingPlanRepository.findByProjectId(projectId);
        return  plans.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()); // Return the first plan or handle as needed
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

        project.setProjectStatus(ProjectStatus.DONATING);
        projectRepository.save(project);
        plan.setEstimatedTotalCost(totalCost);
        plan.setApprovalStatus(SpendingPlanStatus.APPROVED);
        return toResponse(spendingPlanRepository.save(plan));
    }
}
