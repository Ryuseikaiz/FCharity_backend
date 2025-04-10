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
    private BigDecimal minRequiredDonationAmount;
    private BigDecimal estimatedTotalCost;
    private String approvalStatus;
    public SpendingPlanDto(UUID projectId, String planName, String description, BigDecimal minRequiredDonationAmount, BigDecimal estimatedTotalCost, String approvalStatus) {
        this.projectId = projectId;
        this.planName = planName;
        this.description = description;
        this.minRequiredDonationAmount = minRequiredDonationAmount;
        this.estimatedTotalCost = estimatedTotalCost;
        this.approvalStatus = approvalStatus;
    }
}

