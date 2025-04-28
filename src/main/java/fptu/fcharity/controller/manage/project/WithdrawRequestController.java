package fptu.fcharity.controller.manage.project;

import fptu.fcharity.response.project.WithdrawRequestResponse;
import fptu.fcharity.service.manage.project.ProjectWithdrawRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/withdraw-requests")
public class WithdrawRequestController {
    @Autowired
    private ProjectWithdrawRequestService withdrawRequestService;

    @GetMapping
    public List<WithdrawRequestResponse> getAllWithdrawRequests() {
        return withdrawRequestService.getAllWithdrawRequests();
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<?> getAllWithdrawProjectId(@PathVariable UUID id) {
        return ResponseEntity.ok(withdrawRequestService.getWithdrawRequestByProjectId(id));
    }
    @GetMapping("/{id}")
    public WithdrawRequestResponse getWithdrawRequestById(@PathVariable UUID id) {
        return withdrawRequestService.getWithdrawRequestById(id);
    }
    @PostMapping("/create")
    public WithdrawRequestResponse createWithdrawRequest(
            @RequestParam UUID projectId,
            @RequestParam String bankBin,
            @RequestParam String accountNumber,
            @RequestParam String accountHolder
    ) {
        return withdrawRequestService.createWithdrawRequest(projectId, bankBin, accountNumber, accountHolder);
    }

    @PutMapping("/{id}/update-bank-info")
    public WithdrawRequestResponse updateBankInfo(
            @PathVariable UUID id,
            @RequestParam String bankBin,
            @RequestParam String accountNumber,
            @RequestParam String accountHolder
    ) {
        return withdrawRequestService.updateBankInfo(id, bankBin, accountNumber, accountHolder);
    }

    @PutMapping("/{id}/update-transaction-image")
    public WithdrawRequestResponse updateWithdrawImage(
            @PathVariable UUID id,
            @RequestParam(required = false) String transactionImage,
            @RequestParam(required = false) String note
    ) {
        return withdrawRequestService.updateWithdrawImage(id, transactionImage, note);
    }

    @PutMapping("/{id}/update-confirm")
    public WithdrawRequestResponse updateConfirmWithdraw(@PathVariable UUID id) {
        return withdrawRequestService.updateConfirmWithdraw(id);
    }

    @PutMapping("/{id}/update-error")
    public WithdrawRequestResponse updateErrorWithdraw(
            @PathVariable UUID id,
            @RequestParam String note
    ) {
        return withdrawRequestService.updateErrorWithdraw(id, note);
    }
}
