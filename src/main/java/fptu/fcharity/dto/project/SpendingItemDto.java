package fptu.fcharity.dto.project;

import fptu.fcharity.entity.SpendingItem;
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
public class SpendingItemDto {
        private UUID spendingPlanId;
        private String itemName;
        private BigDecimal estimatedCost;
        private String note;
        public SpendingItemDto(SpendingItem spendingItem) {
            this.itemName = spendingItem.getItemName();
            this.estimatedCost = spendingItem.getEstimatedCost();
            this.note = spendingItem.getNote();
            this.spendingPlanId = spendingItem.getSpendingPlan().getId();
        }
        public SpendingItemDto(UUID spendingPlanId, String itemName, BigDecimal estimatedCost, String note) {
            this.spendingPlanId = spendingPlanId;
            this.itemName = itemName;
            this.estimatedCost = estimatedCost;
            this.note = note;
        }
}
