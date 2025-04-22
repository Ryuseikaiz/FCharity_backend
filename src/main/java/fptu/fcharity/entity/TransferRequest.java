package fptu.fcharity.entity;

import fptu.fcharity.utils.constants.project.TransferRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@Table(name = "transfer_request")
public class TransferRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "request_id", nullable = false)
    private HelpRequest request;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Nationalized
    @Column(name = "reason", nullable = false, length = 500)
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
    @Column(name = "transaction_code", length = 100)
    private String transactionCode;

    @Nationalized
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @ColumnDefault("getdate()")
    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @ColumnDefault("getdate()")
    @Column(name = "updated_date", nullable = false)
    private Instant updatedDate;

    public TransferRequest(HelpRequest request, Project project, BigDecimal amount,
                           String reason, String note, String status
                           ) {
        this.request = request;
        this.project = project;
        this.amount = amount;
        this.reason = reason;
        this.status = status;
        this.note = note;
        this.createdDate = Instant.now();
        this.updatedDate = Instant.now();
    }
}