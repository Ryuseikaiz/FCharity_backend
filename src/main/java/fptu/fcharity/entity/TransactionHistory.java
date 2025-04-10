package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "transaction_history")
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "target_wallet_id")
    private Wallet targetWallet;

    @Column(name = "amount")
    private BigDecimal amount;

    @Nationalized
    @Column(name = "transaction_type", length = 50)
    private String transactionType;

    @Column(name = "transaction_date")
    private Instant transactionDate;

    public TransactionHistory(Wallet wallet, BigDecimal amount, String deposit, Instant transactionDate,Wallet targetWallet) {
        this.wallet = wallet;
        this.amount = amount;
        this.transactionType = deposit;
        this.transactionDate = transactionDate;
        this.targetWallet = targetWallet;
    }
}