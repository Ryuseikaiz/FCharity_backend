package fptu.fcharity.response.project;

import fptu.fcharity.entity.SpendingPlan;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
public class SpendingPlanResponse {
    private UUID id;
    private UUID projectId;
    private String planName;
    private String description;
    private BigDecimal maxExtraCostPercentage;
    private BigDecimal estimatedTotalCost;
    private String approvalStatus;
    private Instant createdDate;
    private Instant updatedDate;
    public SpendingPlanResponse(SpendingPlan spendingPlan) {
        this.id = spendingPlan.getId();
        this.projectId = spendingPlan.getProject().getId();
        this.planName = spendingPlan.getPlanName();
        this.description = spendingPlan.getDescription();
        this.maxExtraCostPercentage = spendingPlan.getMaxExtraCostPercentage();
        this.estimatedTotalCost = spendingPlan.getEstimatedTotalCost();
        this.approvalStatus = spendingPlan.getApprovalStatus();
        this.createdDate = spendingPlan.getCreatedDate();
        this.updatedDate = spendingPlan.getUpdatedDate();
    }

}

