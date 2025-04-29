package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "organization_transaction_history")
public class OrganizationTransactionHistory {
    @Id
    @GeneratedValue(generator = "UUID")
    @ColumnDefault("newid()")
    @Column(name = "transaction_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_id")
    private Project project;

    @Nationalized
    @Column(name = "transaction_status", length = 50)
    private String transactionStatus;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Nationalized
    @Column(name = "message")
    private String message;

    @Column(name = "transaction_time")
    private Instant transactionTime;

    @Nationalized
    @Column(name = "transaction_type", length = 50)
    private String transactionType;

}

//public class OrganizationTransactionType {
//    public static final String EXTRACT_EXTRA_COST = "EXTRACT_EXTRA_COST";
//    public static final String ALLOCATE_EXTRA_COST = "ALLOCATE_EXTRA_COST";
//}