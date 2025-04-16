package fptu.fcharity.controller.manage.project;

import fptu.fcharity.response.project.TransferRequestResponse;
import fptu.fcharity.service.manage.project.TransferRequestService;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transfer-requests")
public class TransferRequestController {
    @Autowired
    private TransferRequestService transferRequestService;

    @GetMapping
    public ResponseEntity<?> getAllTransferRequests() {
        try {
            return ResponseEntity.ok(transferRequestService.getAllTransferRequests());
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<TransferRequestResponse> getTransferRequestById(@PathVariable UUID id) {
        try {
            TransferRequestResponse response = transferRequestService.getTransferRequestById(id);
            return ResponseEntity.ok(response);
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}/update-bank-info")
    public ResponseEntity<TransferRequestResponse> updateBankInfo(
            @PathVariable UUID id,
            @RequestParam String bankBin,
            @RequestParam String accountNumber,
            @RequestParam String accountHolder) {
        try {
            TransferRequestResponse response = transferRequestService.updateBankInfo(id, bankBin, accountNumber, accountHolder);
            return ResponseEntity.ok(response);
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}/update-transaction-image")
    public ResponseEntity<TransferRequestResponse> updateTransferImage(
            @PathVariable UUID id,
            @RequestParam String transactionImage,
            @RequestParam String note) {
        try {
            TransferRequestResponse response = transferRequestService.updateTransferImage(id, transactionImage,note);
            return ResponseEntity.ok(response);
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}/update-confirm-transfer")
    public ResponseEntity<TransferRequestResponse> updateConfirmTransfer(@PathVariable UUID id) {
        try {
            TransferRequestResponse response = transferRequestService.updateConfirmTransfer(id);
            return ResponseEntity.ok(response);
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}/update-error-transfer")
    public ResponseEntity<TransferRequestResponse> updateErrorTransfer(
            @PathVariable UUID id,
            @RequestParam String note) {
        try {
            TransferRequestResponse response = transferRequestService.updateErrorTransfer(id, note);
            return ResponseEntity.ok(response);
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
