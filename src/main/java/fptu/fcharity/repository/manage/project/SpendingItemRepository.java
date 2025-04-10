package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.SpendingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Repository
public interface SpendingItemRepository extends JpaRepository<SpendingItem, UUID> {
    @Query("SELECT si FROM SpendingItem si JOIN FETCH si.spendingPlan sp WHERE sp.id = :planId")
    List<SpendingItem> findBySpendingPlanId(UUID planId);
    // Define any custom query methods if needed
    // For example:
    // List<SpendingItem> findBySpendingPlanId(UUID spendingPlanId);
}
