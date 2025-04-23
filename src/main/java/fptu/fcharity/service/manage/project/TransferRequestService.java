package fptu.fcharity.service.manage.project;

import fptu.fcharity.entity.*;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.project.*;
import fptu.fcharity.repository.manage.request.RequestRepository;
import fptu.fcharity.response.project.TransferRequestResponse;
import fptu.fcharity.service.HelpNotificationService;
import fptu.fcharity.utils.constants.project.ProjectStatus;
import fptu.fcharity.utils.constants.project.TransferRequestStatus;
import fptu.fcharity.utils.constants.request.RequestStatus;
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
    @Autowired
    private RequestRepository requestRepository;

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
        //send to user,admin
        notificationService.notifyUser(
                transferRequest.getRequest().getUser(),
                "Bank information submitted for transfer request",
                null,
                "You have submitted your bank account details for the transfer request of request '" + transferRequest.getRequest().getTitle()+ "'. Please wait for admin approval.",
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
        spendingDetail.setProject(p);
        spendingDetail.setTransactionTime(Instant.now());
        spendingDetail.setProofImage(transferRequest.getTransactionImage());
        spendingDetailRepository.save(spendingDetail);

        Wallet wallet = p.getWalletAddress();
        wallet.setBalance(
                p.getWalletAddress().getBalance().subtract(spendingDetail.getAmount())
        );
        walletRepository.save(wallet);

        p.setProjectStatus(ProjectStatus.FINISHED);
        p.setActualEndTime(Instant.now());
        projectRepository.save(p);

        p.getRequest().setStatus(RequestStatus.COMPLETED);
        requestRepository.save(p.getRequest());

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
