package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transaction_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "transaction_date")
    private Instant transactionDate;

    @Column(name = "transaction_type")
    private String transactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_wallet_id", referencedColumnName = "wallet_id", nullable = false)
    private Wallet targetWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", referencedColumnName = "wallet_id", nullable = false)
    private Wallet wallet;
}

//public class TransactionType {
//    public static final String DEPOSIT = "DEPOSIT";
//    public static final String WITHDRAW = "WITHDRAW";
//    public static final String DONATE_PROJECT = "DONATE_PROJECT";
//}