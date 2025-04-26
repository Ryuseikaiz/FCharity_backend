package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "project_withdraw_requests")
public class ProjectWithdrawRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Nationalized
    @Column(name = "reason",length = 500)
    private String reason;

    @Nationalized
    @Column(name = "note", length = 1000)
    private String note;

    @Nationalized
    @Column(name = "bank_account", length = 100)
    private String bankAccount;

    @Nationalized
    @Column(name = "bank_bin", length = 100)
    private String bankBin;

    @Nationalized
    @Column(name = "bank_owner", length = 100)
    private String bankOwner;

    @Nationalized
    @Column(name = "transaction_image", length = 500)
    private String transactionImage;

    @Nationalized
    @Column(name = "status", nullable = true, length = 50)
    private String status;

    @ColumnDefault("getdate()")
    @Column(name = "created_date", nullable = true)
    private Instant createdDate;

    @ColumnDefault("getdate()")
    @Column(name = "updated_date", nullable = true)
    private Instant updatedDate;

}