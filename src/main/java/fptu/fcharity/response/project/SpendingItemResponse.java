package fptu.fcharity.response.project;

import fptu.fcharity.entity.SpendingItem;
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
public class SpendingItemResponse {
    private UUID id;
    private UUID spendingPlanId;
    private String itemName;
    private BigDecimal estimatedCost;
    private String note;
    private Instant createdDate;
    private Instant updatedDate;
    public SpendingItemResponse( SpendingItem i ){
        this.id = i.getId();
        this.spendingPlanId = i.getSpendingPlan().getId();
        this.itemName = i.getItemName();
        this.estimatedCost = i.getEstimatedCost();
        this.note = i.getNote();
        this.createdDate = i.getCreatedDate();
        this.updatedDate = i.getUpdatedDate();
    }
}
