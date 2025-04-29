package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.OrganizationTransactionHistory;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationTransactionHistoryRepository extends JpaRepository<OrganizationTransactionHistory, UUID> {
    List<OrganizationTransactionHistory> findByOrganizationOrganizationId(UUID organizationOrganizationId);

    List<OrganizationTransactionHistory> findByOrganizationOrganizationIdAndTransactionType(UUID organizationOrganizationId, String transactionType);
}
