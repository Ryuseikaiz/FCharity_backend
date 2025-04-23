package fptu.fcharity.response.project;

import fptu.fcharity.entity.HelpRequest;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.TransferRequest;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransferRequestResponse {
    private UUID id;
    private ProjectResponse project;

    private UUID requestId;
    private String requestTitle;

    private BigDecimal amount;

    private String reason;

    private String note;

    private String bankAccount;

    private String bankBin;

    private String bankOwner;

    private String transactionImage;


    private String status;

    private Instant createdDate;

    private Instant updatedDate;
    public TransferRequestResponse(TransferRequest transferRequest) {
        this.id = transferRequest.getId();
        this.project = new ProjectResponse(transferRequest.getProject());
        if(transferRequest.getRequest() != null) {
            this.requestId = transferRequest.getRequest().getId();
            this.requestTitle = transferRequest.getRequest().getTitle();
        }
        this.amount = transferRequest.getAmount();
        this.reason = transferRequest.getReason();
        this.note = transferRequest.getNote();
        this.bankAccount = transferRequest.getBankAccount();
        this.bankBin = transferRequest.getBankBin();
        this.bankOwner = transferRequest.getBankOwner();
        this.transactionImage = transferRequest.getTransactionImage();
        this.status = transferRequest.getStatus();
        this.createdDate = transferRequest.getCreatedDate();
        this.updatedDate = transferRequest.getUpdatedDate();
    }
}
