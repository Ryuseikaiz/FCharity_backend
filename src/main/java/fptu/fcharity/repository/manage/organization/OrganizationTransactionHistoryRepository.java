package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.OrganizationTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizationTransactionHistoryRepository extends JpaRepository<OrganizationTransactionHistory, UUID> {
}
