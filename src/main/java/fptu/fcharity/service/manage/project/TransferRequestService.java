package fptu.fcharity.service.manage.project;

import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.SpendingDetail;
import fptu.fcharity.entity.SpendingItem;
import fptu.fcharity.entity.SpendingPlan;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.project.*;
import fptu.fcharity.response.project.TransferRequestResponse;
import fptu.fcharity.service.HelpNotificationService;
import fptu.fcharity.utils.constants.project.ProjectStatus;
import fptu.fcharity.utils.constants.project.TransferRequestStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TransferRequestService {
    @Autowired
    private TransferRequestRepository transferRequestRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SpendingPlanRepository spendingPlanRepository;
    @Autowired
    private SpendingItemRepository spendingItemRepository;
    @Autowired
    private SpendingDetailRepository spendingDetailRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private HelpNotificationService notificationService;
    public List<TransferRequestResponse> getAllTransferRequests() {
        return transferRequestRepository.findAll().stream()
                .map(TransferRequestResponse::new)
                .toList();
    }

    public TransferRequestResponse getTransferRequestById(UUID id) {
        var transferRequest = transferRequestRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Transfer request not found"));
        return new TransferRequestResponse(transferRequest);
    }
    public TransferRequestResponse updateBankInfo(UUID id, String bankBin, String accountNumber, String accountHolder) {
        var transferRequest = transferRequestRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Transfer request not found"));
        transferRequest.setBankBin(bankBin);
        transferRequest.setBankAccount(accountNumber);
        transferRequest.setBankOwner(accountHolder);
        transferRequest.setStatus(TransferRequestStatus.PENDING_ADMIN_APPROVAL);
        transferRequest.setUpdatedDate(Instant.now());
        //send to admin
        notificationService.notifyUser(
                transferRequest.getRequest().getUser(),
                "Updated transfer information from request: " + transferRequest.getRequest().getTitle(),
                null,
                "Transfer request",
                "/user/manage-profile/myrequests"
        );
        return new TransferRequestResponse(transferRequestRepository.save(transferRequest));
    }
    public TransferRequestResponse updateTransferImage(UUID id,String transactionImage,String note) {
        var transferRequest = transferRequestRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Transfer request not found"));
        transferRequest.setStatus(TransferRequestStatus.CONFIRM_SENT);
        if(transactionImage!=null) transferRequest.setTransactionImage(transactionImage);
        if(note!=null) transferRequest.setNote(note);
        transferRequest.setUpdatedDate(Instant.now());
        notificationService.notifyUser(
                transferRequest.getRequest().getUser(),
                "Confirm received donation's fund for request '" + transferRequest.getRequest().getTitle()+"'",
                null,
                "Donation's fund has been sent. Please check your account's balance to confirm.",
                "/user/manage-profile/myrequests"
        );
        return new TransferRequestResponse(transferRequestRepository.save(transferRequest));
    }
    public TransferRequestResponse updateConfirmTransfer(UUID id) {
        var transferRequest = transferRequestRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Transfer request not found"));
        transferRequest.setStatus(TransferRequestStatus.COMPLETED);
        transferRequest.setUpdatedDate(Instant.now());
        // Update project status and create spending item
        Project p = transferRequest.getProject();
        SpendingPlan plan = spendingPlanRepository.findByProjectId(p.getId());
        BigDecimal totalDonations = p.getWalletAddress().getBalance();
        SpendingItem item = new SpendingItem();
        item.setItemName("Send money to requester");
        item.setEstimatedCost(totalDonations);
        item.setNote("Send money to requester");
        item.setSpendingPlan(plan);
        item.setCreatedDate(Instant.now());
        spendingItemRepository.save(item);

        SpendingDetail spendingDetail = new SpendingDetail();
        spendingDetail.setSpendingItem(item);
        spendingDetail.setAmount(item.getEstimatedCost());
        spendingDetail.setDescription("Send money to requester");
        spendingDetail.setTransactionTime(Instant.now());
        spendingDetailRepository.save(spendingDetail);

        p.getWalletAddress().setBalance(
                p.getWalletAddress().getBalance().subtract(spendingDetail.getAmount())
        );
        walletRepository.save(p.getWalletAddress());

        p.setProjectStatus(ProjectStatus.FINISHED);
        p.setActualEndTime(Instant.now());
        projectRepository.save(p);

        notificationService.notifyUser(
                p.getLeader(),
                "Requester has received the funds, and your project is now completed.",
                null,
                "The transfer request has been successfully processed. The requester has received the funds, and the project has been confirmed as completed.",
                "/manage-project/"+p.getId()+"/home"
        );
        return new TransferRequestResponse(transferRequestRepository.save(transferRequest));
    }
    public TransferRequestResponse updateErrorTransfer(UUID id,String note) {
        var transferRequest = transferRequestRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Transfer request not found"));
        transferRequest.setStatus(TransferRequestStatus.ERROR);
        transferRequest.setNote(note);
        transferRequest.setUpdatedDate(Instant.now());
        return new TransferRequestResponse(transferRequestRepository.save(transferRequest));
    }
}
