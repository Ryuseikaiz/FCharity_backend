package fptu.fcharity.dto.project;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
public class SpendingPlanDto {
    private UUID projectId;
    private String planName;
    private String description;
    private BigDecimal maxExtraCostPercentage;
    private BigDecimal estimatedTotalCost;
    private String approvalStatus;
    public SpendingPlanDto(UUID projectId, String planName, String description, BigDecimal maxExtraCostPercentage, BigDecimal estimatedTotalCost, String approvalStatus) {
        this.projectId = projectId;
        this.planName = planName;
        this.description = description;
        this.maxExtraCostPercentage = maxExtraCostPercentage;
        this.estimatedTotalCost = estimatedTotalCost;
        this.approvalStatus = approvalStatus;
    }
}

