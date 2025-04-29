package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationTransactionHistoryDTO;
import fptu.fcharity.dto.organization.ProjectExtraFundRequestDTO;
import fptu.fcharity.dto.organization.ToOrganizationDonationDTO;
import fptu.fcharity.entity.OrganizationTransactionHistory;
import fptu.fcharity.entity.ProjectExtraFundRequest;
import fptu.fcharity.entity.ToOrganizationDonation;
import fptu.fcharity.service.manage.organization.finance.OrganizationFinanceService;
import fptu.fcharity.utils.mapper.organization.OrganizationTransactionHistoryMapper;
import fptu.fcharity.utils.mapper.organization.ProjectExtraFundRequestMapper;
import fptu.fcharity.utils.mapper.organization.ToOrganizationDonationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/finance")
public class OrganizationFinanceController {
    private final OrganizationFinanceService organizationFinanceService;
    private final OrganizationTransactionHistoryMapper organizationTransactionHistoryMapper;
    private final ToOrganizationDonationMapper toOrganizationDonationMapper;
    private final ProjectExtraFundRequestMapper projectExtraFundRequestMapper;

    @Autowired
    public OrganizationFinanceController(
            OrganizationFinanceService organizationFinanceService,
            OrganizationTransactionHistoryMapper organizationTransactionHistoryMapper,
            ToOrganizationDonationMapper toOrganizationDonationMapper,
            ProjectExtraFundRequestMapper projectExtraFundRequestMapper) {
        this.organizationFinanceService = organizationFinanceService;
        this.organizationTransactionHistoryMapper = organizationTransactionHistoryMapper;
        this.toOrganizationDonationMapper = toOrganizationDonationMapper;
        this.projectExtraFundRequestMapper = projectExtraFundRequestMapper;
    }

    @GetMapping("/organizations/{organizationId}/totalIncome")
    public BigDecimal getTotalIncome(@PathVariable("organizationId") UUID organizationId) {
        System.out.println("get Total Income organizationId: " + organizationId);
        return organizationFinanceService.getTotalIncome(organizationId);
    }

    @GetMapping("/organizations/{organizationId}/totalExpense")
    public BigDecimal getTotalExpense(@PathVariable("organizationId") UUID organizationId) {
        return organizationFinanceService.getTotalExpense(organizationId);
    }

    @GetMapping("/organizations/{organizationId}/donates")
    public List<ToOrganizationDonationDTO> getDonatesByOrganizationId(@PathVariable("organizationId") UUID organizationId) {
        return organizationFinanceService.getDonatesByOrganizationId(organizationId)
                .stream().map(toOrganizationDonationMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/organizations/{organizationId}/transactions")
    public List<OrganizationTransactionHistoryDTO> getTransactionsByOrganizationId(@PathVariable("organizationId") UUID organizationId) {
        return organizationFinanceService.getTransactionsByOrganizationId(organizationId)
                .stream().map(organizationTransactionHistoryMapper::toDTO).collect(Collectors.toList());
    }

    @PostMapping("/organizations/transactions")
    public OrganizationTransactionHistoryDTO createTransaction(@RequestBody OrganizationTransactionHistory organizationTransactionHistory) {
        return organizationTransactionHistoryMapper.toDTO(organizationFinanceService.createTransaction(organizationTransactionHistory));
    }

    @GetMapping("/organizations/{organizationId}/extraFundRequests")
    public List<ProjectExtraFundRequestDTO> getExtraFundRequestsByOrganizationId(@PathVariable("organizationId") UUID organizationId) {
        return organizationFinanceService.getExtraFundRequestsByOrganizationId(organizationId).stream()
                .map(projectExtraFundRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

}
