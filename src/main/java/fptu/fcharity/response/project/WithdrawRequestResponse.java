package fptu.fcharity.response.project;

import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.ProjectWithdrawRequest;
import fptu.fcharity.repository.manage.project.ProjectWithdrawRequestRepository;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class WithdrawRequestResponse {
    private UUID id;
    private ProjectResponse project;

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
    public WithdrawRequestResponse(ProjectWithdrawRequest request) {
        this.id = request.getId();
        this.project = new ProjectResponse(request.getProject());
        this.amount = request.getAmount();
        this.reason = request.getReason();
        this.note = request.getNote();
        this.bankAccount = request.getBankAccount();
        this.bankBin = request.getBankBin();
        this.bankOwner = request.getBankOwner();
        this.transactionImage = request.getTransactionImage();
        this.status = request.getStatus();
        this.createdDate = request.getCreatedDate();
        this.updatedDate = request.getUpdatedDate();
    }

    public WithdrawRequestResponse(ProjectWithdrawRequestRepository projectWithdrawRequestRepository) {
    }
}
