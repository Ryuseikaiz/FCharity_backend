package fptu.fcharity.service.manage.organization.finance;

import fptu.fcharity.entity.OrganizationTransactionHistory;
import fptu.fcharity.entity.ProjectExtraFundRequest;
import fptu.fcharity.entity.ToOrganizationDonation;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface OrganizationFinanceService {
    BigDecimal getTotalIncome(UUID organizationId);
    BigDecimal getTotalExpense(UUID organizationId);
    List<ToOrganizationDonation> getDonatesByOrganizationId(UUID organizationId);
    List<OrganizationTransactionHistory> getTransactionsByOrganizationId(UUID organizationId);
    OrganizationTransactionHistory createTransaction(OrganizationTransactionHistory organizationTransactionHistory);
    List<ProjectExtraFundRequest> getExtraFundRequestsByOrganizationId(UUID organizationId);
}
