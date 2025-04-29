package fptu.fcharity.service.manage.organization.finance;

import fptu.fcharity.entity.OrganizationTransactionHistory;
import fptu.fcharity.entity.ProjectExtraFundRequest;
import fptu.fcharity.entity.ToOrganizationDonation;
import fptu.fcharity.repository.manage.organization.OrganizationTransactionHistoryRepository;
import fptu.fcharity.repository.manage.organization.ToOrganizationDonationRepository;
import fptu.fcharity.repository.manage.project.ProjectExtraFundRequestRepository;
import fptu.fcharity.utils.constants.organization.OrganizationTransactionType;
import fptu.fcharity.utils.constants.request.RequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrganizationFinanceServiceImpl implements OrganizationFinanceService {
    private final ToOrganizationDonationRepository toOrganizationDonationRepository;
    private final OrganizationTransactionHistoryRepository organizationTransactionHistoryRepository;
    private final ProjectExtraFundRequestRepository projectExtraFundRequestRepository;
    @Autowired
    public OrganizationFinanceServiceImpl(
            ToOrganizationDonationRepository toOrganizationDonationRepository,
            OrganizationTransactionHistoryRepository organizationTransactionHistoryRepository,
            ProjectExtraFundRequestRepository projectExtraFundRequestRepository

    ) {
        this.toOrganizationDonationRepository = toOrganizationDonationRepository;
        this.organizationTransactionHistoryRepository = organizationTransactionHistoryRepository;
        this.projectExtraFundRequestRepository = projectExtraFundRequestRepository;
    }

    @Override
    public BigDecimal getTotalIncome(UUID organizationId) {
        BigDecimal totalIncome = organizationTransactionHistoryRepository.findByOrganizationOrganizationIdAndTransactionType(organizationId, OrganizationTransactionType.EXTRACT_EXTRA_COST).stream()
                .map(OrganizationTransactionHistory::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalIncome;
    }

    @Override
    public BigDecimal getTotalExpense(UUID organizationId) {
        BigDecimal totalExpense = organizationTransactionHistoryRepository.findByOrganizationOrganizationIdAndTransactionType(organizationId,OrganizationTransactionType.ALLOCATE_EXTRA_COST).stream()
                .map(OrganizationTransactionHistory::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalExpense;
    }

    @Override
    public List<ToOrganizationDonation> getDonatesByOrganizationId(UUID organizationId) {
        return toOrganizationDonationRepository.findByOrganizationOrganizationId(organizationId);
    }

    @Override
    public List<OrganizationTransactionHistory> getTransactionsByOrganizationId(UUID organizationId) {
        return organizationTransactionHistoryRepository.findByOrganizationOrganizationId(organizationId);
    }

    @Override
    public OrganizationTransactionHistory createTransaction(OrganizationTransactionHistory organizationTransactionHistory) {
        return organizationTransactionHistoryRepository.save(organizationTransactionHistory);
    }

    @Override
    public List<ProjectExtraFundRequest> getExtraFundRequestsByOrganizationId(UUID organizationId) {
        return projectExtraFundRequestRepository.findProjectExtraFundRequestByOrganizationOrganizationIdAndStatus(organizationId, RequestStatus.PENDING);
    }
}
