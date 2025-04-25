package fptu.fcharity.service.manage.project;

import fptu.fcharity.entity.*;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.organization.OrganizationTransactionHistoryRepository;
import fptu.fcharity.repository.manage.project.*;
import fptu.fcharity.response.project.ExtraFundRequestDto;
import fptu.fcharity.response.project.ExtraFundRequestResponse;
import fptu.fcharity.utils.constants.organization.OrganizationTransactionType;
import fptu.fcharity.utils.constants.request.RequestStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class ProjectExtraFundRequestService {
    @Autowired
    private
    ProjectExtraFundRequestRepository projectExtraFundRequestRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private SpendingPlanRepository spendingPlanRepository;
    @Autowired
    private SpendingItemRepository spendingItemRepository;
    @Autowired
    private ToProjectDonationRepository toProjectDonationRepository;
    @Autowired
    private OrganizationTransactionHistoryRepository organizationTransactionHistoryRepository;
    @Autowired
    private ToProjectDonationService toProjectDonationService;
    @Autowired
    private WalletRepository walletRepository;

    public void takeObject(ProjectExtraFundRequest p, ExtraFundRequestDto dto){
        if(dto.getProjectId()!=null){
            p.setProject(projectRepository.findWithEssentialById(dto.getProjectId()));
        }
        if(dto.getOrganizationId()!=null){
            p.setOrganization(organizationRepository.findById(dto.getOrganizationId())
                    .orElseThrow(() -> new ApiRequestException("Organization not found")));
        }
    }
    public ExtraFundRequestResponse createExtraFundRequest(ExtraFundRequestDto dto){
        ProjectExtraFundRequest existing = projectExtraFundRequestRepository.findEssentialById(dto.getId());
      if(existing!=null){
          projectExtraFundRequestRepository.delete(existing);
      }
    ProjectExtraFundRequest request = new ProjectExtraFundRequest();
    takeObject(request, dto);
    Project p = request.getProject();
    SpendingPlan plan = spendingPlanRepository.findByProjectId(p.getId());
    BigDecimal totalDonations = toProjectDonationRepository.findByProjectId(p.getId()).stream()
            .map(ToProjectDonation::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalSpending = spendingItemRepository.findBySpendingPlanId(plan.getId()).stream()
            .map(SpendingItem::getEstimatedCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    request.setAmount(totalDonations.subtract(totalSpending));
    request.setReason(dto.getReason());
    request.setProofImage(dto.getProofImage());
    request.setStatus(RequestStatus.PENDING);
    request.setCreatedDate(Instant.now());
    return new ExtraFundRequestResponse(projectExtraFundRequestRepository.save(request));
}
    public ExtraFundRequestResponse approveExtraFundRequest(ExtraFundRequestDto dto){
        ProjectExtraFundRequest request = projectExtraFundRequestRepository.findById(dto.getId())
                .orElseThrow(() -> new ApiRequestException("Extra fund request not found"));
        request.setProofImage(dto.getProofImage());
        request.setStatus(RequestStatus.APPROVED);
        request.setUpdatedDate(Instant.now());
        //new transaction history org
        OrganizationTransactionHistory history = new OrganizationTransactionHistory();
        history.setOrganization(request.getOrganization());
        history.setProject(request.getProject());
        history.setTransactionStatus(RequestStatus.APPROVED);
        history.setTransactionType(OrganizationTransactionType.ALLOCATE_EXTRA_COST);
        history.setAmount(request.getAmount());
        history.setTransactionTime(Instant.now());
        history.setMessage("Allocated extra fund for project: " + request.getProject().getProjectName());
        organizationTransactionHistoryRepository.save(history);
       //update wallet project
        Project p = request.getProject();
        Wallet pWallet = p.getWalletAddress();
        pWallet.setBalance(pWallet.getBalance().add(request.getAmount()));
        walletRepository.save(pWallet);
        return new ExtraFundRequestResponse(projectExtraFundRequestRepository.save(request));
    }
    public ExtraFundRequestResponse rejectExtraFundRequest(ExtraFundRequestDto dto){
        ProjectExtraFundRequest request = projectExtraFundRequestRepository.findById(dto.getId())
                .orElseThrow(() -> new ApiRequestException("Extra fund request not found"));
        request.setReason(dto.getReason());
        request.setStatus(RequestStatus.REJECTED);
        request.setUpdatedDate(Instant.now());
        return new ExtraFundRequestResponse(projectExtraFundRequestRepository.save(request));
    }
}
