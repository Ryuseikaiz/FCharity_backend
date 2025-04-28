package fptu.fcharity.service.manage.project;

import fptu.fcharity.entity.*;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.project.*;
import fptu.fcharity.repository.manage.request.RequestRepository;
import fptu.fcharity.response.project.TransferRequestResponse;
import fptu.fcharity.response.project.WithdrawRequestResponse;
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
public class ProjectWithdrawRequestService {
    @Autowired
    private ProjectWithdrawRequestRepository withdrawRequestRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private HelpNotificationService notificationService;


    public List<WithdrawRequestResponse> getAllWithdrawRequests() {
        return withdrawRequestRepository.findAll().stream()
                .map(WithdrawRequestResponse::new)
                .toList();
    }

    public WithdrawRequestResponse getWithdrawRequestById(UUID id) {
        var request = withdrawRequestRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Withdraw request not found"));
        if(request ==null) return null;
        return new WithdrawRequestResponse(request);
    }
    public WithdrawRequestResponse createWithdrawRequest( UUID projectId, String bankBin, String accountNumber, String accountHolder) {
        var request = new ProjectWithdrawRequest();
        Project p = projectRepository.findWithEssentialById(projectId);
        request.setProject(p);
        request.setBankBin(bankBin);
        request.setAmount(p.getWalletAddress().getBalance());
        request.setBankAccount(accountNumber);
        request.setBankOwner(accountHolder);
        request.setStatus(TransferRequestStatus.PENDING_ADMIN_APPROVAL);
        request.setCreatedDate(Instant.now());
        //send to user, admin
        notificationService.notifyUser(
                request.getProject().getLeader(),
                "Bank information submitted for withdrawal request",
                null,
                "You have submitted your bank account details for the withdrawal request of project '" + request.getProject().getProjectName() + "'. Please wait for admin approval.",
                "/manage-project" + request.getProject().getId() + "/finance"
        );
        return new WithdrawRequestResponse(withdrawRequestRepository.save(request));
    }
    public WithdrawRequestResponse updateBankInfo(UUID id, String bankBin, String accountNumber, String accountHolder) {
        var request = withdrawRequestRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Withdraw request not found"));
        request.setBankBin(bankBin);
        request.setBankAccount(accountNumber);
        request.setBankOwner(accountHolder);
        request.setStatus(TransferRequestStatus.PENDING_ADMIN_APPROVAL);
        request.setUpdatedDate(Instant.now());
        //send to user, admin
        notificationService.notifyUser(
                request.getProject().getLeader(),
                "Bank information submitted for withdrawal request",
                null,
                "You have submitted your bank account details for the withdrawal request of project '" + request.getProject().getProjectName() + "'. Please wait for admin approval.",
                "/manage-project" + request.getProject().getId() + "/finance"
        );
        return new WithdrawRequestResponse(withdrawRequestRepository.save(request));
    }
    public WithdrawRequestResponse updateWithdrawImage(UUID id,String transactionImage,String note) {
        var request = withdrawRequestRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Withdraw request not found"));
        request.setStatus(TransferRequestStatus.CONFIRM_SENT);
        if(transactionImage!=null) request.setTransactionImage(transactionImage);
        if(note!=null) request.setNote(note);
        request.setUpdatedDate(Instant.now());
        notificationService.notifyUser(
                request.getProject().getLeader(),
                "Request to confirm fund receipt",
                null,
                "The admin has confirmed that the donation funds for project '" + request.getProject().getProjectName() + "' have been transferred. Please check your bank account and confirm in the system if you've received the amount.",
                "/manage-project" + request.getProject().getId() + "/finance"
        );

        return new WithdrawRequestResponse(withdrawRequestRepository.save(request));
    }
    public WithdrawRequestResponse updateConfirmWithdraw(UUID id) {
        var request = withdrawRequestRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Withdraw request not found"));
        request.setStatus(TransferRequestStatus.COMPLETED);
        request.setUpdatedDate(Instant.now());
        // Update project status and create spending item
        Project p = request.getProject();

        Wallet wallet = p.getWalletAddress();
        wallet.setBalance(new BigDecimal("0"));
        walletRepository.save(wallet);

        notificationService.notifyUser(
                p.getLeader(),
                "Requester has received the funds, and your project is now completed.",
                null,
                "The transfer request has been successfully processed. The requester has received the funds, and the project has been confirmed as completed.",
                "/manage-project/"+p.getId()+"/home"
        );
        return new WithdrawRequestResponse(withdrawRequestRepository.save(request));
    }
    public WithdrawRequestResponse updateErrorWithdraw(UUID id,String note) {
        var request = withdrawRequestRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Withdraw request not found"));
        request.setStatus(TransferRequestStatus.ERROR);
        request.setNote(note);
        request.setUpdatedDate(Instant.now());
        return new WithdrawRequestResponse(withdrawRequestRepository.save(request));
    }

    public WithdrawRequestResponse getWithdrawRequestByProjectId(UUID id) {
       ProjectWithdrawRequest request = withdrawRequestRepository.findByProject_Id(id);
       if(request ==null) return null;
       return new WithdrawRequestResponse(request);
    }
}
