package fptu.fcharity.response.user;

import fptu.fcharity.entity.ToOrganizationDonation;
import fptu.fcharity.entity.ToProjectDonation;
import fptu.fcharity.response.project.ToProjectDonationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TransactionHistoryResponse {
    private UUID id;
    private UUID userId;
    private UUID transactionTargetId;
    private String transactionTargetName;
    private String transactionType;
    private String transactionStatus;
    private Instant transactionTime;
    private BigDecimal transactionAmount;
    private String transactionMessage;
    public TransactionHistoryResponse(ToProjectDonation t){
        this.id = t.getId();
        this.userId = t.getUser().getId();
        this.transactionTargetId = t.getProject().getId();
        this.transactionTargetName = t.getProject().getProjectName();
        this.transactionType = "DONATE_PROJECT";
        this.transactionStatus = t.getDonationStatus();
        this.transactionTime = t.getDonationTime();
        this.transactionAmount = t.getAmount();
        this.transactionMessage = t.getMessage();
    }
    public TransactionHistoryResponse(ToOrganizationDonation t){
        this.id = t.getId();
        this.userId = t.getUser().getId();
        this.transactionTargetId = t.getOrganization().getOrganizationId();
        this.transactionTargetName = t.getOrganization().getOrganizationName();
        this.transactionType = "DONATE_ORGANIZATION";
        this.transactionStatus = t.getDonationStatus();
        this.transactionTime = t.getDonationTime();
        this.transactionAmount = t.getAmount();
        this.transactionMessage = t.getMessage();
    }
}
