package fptu.fcharity.response.user;

import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.TransactionHistory;
import fptu.fcharity.entity.Wallet;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Setter
@Getter
@NoArgsConstructor
public class TransactionHistoryResponse {
    private UUID id;
    private Wallet wallet;
    private Wallet targetWallet;
    private UUID objectId;
    private String objectName;
    private BigDecimal amount;
    private String transactionType;
    private Instant transactionDate;
    public TransactionHistoryResponse(TransactionHistory t, UUID objectId,String objectName){
        this.id = t.getId();
        this.amount = t.getAmount();
        this.wallet = t.getWallet();
        this.targetWallet = t.getTargetWallet();
        this.objectId = objectId;
        this.objectName = objectName;
        this.transactionDate = t.getTransactionDate();
        this.transactionType = t.getTransactionType();
    }
}
