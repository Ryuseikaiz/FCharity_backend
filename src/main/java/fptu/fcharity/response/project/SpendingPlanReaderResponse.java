package fptu.fcharity.response.project;

import fptu.fcharity.entity.SpendingItem;
import fptu.fcharity.entity.SpendingPlan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class SpendingPlanReaderResponse {
    private SpendingPlan spendingPlan;
    private List<SpendingItem> spendingItems;
    private List<String> errors;
    public SpendingPlanReaderResponse() {
        this.spendingPlan = new SpendingPlan(); // Initialize
        this.spendingItems = new ArrayList<>();
        this.errors = new ArrayList<>();
    }
    public boolean hasErrors() {
        return this.errors != null && !this.errors.isEmpty();
    }
}
