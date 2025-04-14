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
@Table(name = "spending_plans")
public class SpendingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "spending_plan_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Nationalized
    @Column(name = "plan_name")
    private String planName;

    @Nationalized
    @Column(name = "description")
    private String description;

    @ColumnDefault("getdate()")
    @Column(name = "created_date")
    private Instant createdDate;

    @ColumnDefault("getdate()")
    @Column(name = "updated_date")
    private Instant updatedDate;

    @Column(name = "max_extra_cost_percentage", precision = 18, scale = 2)
    private BigDecimal maxExtraCostPercentage;

    @Column(name = "estimated_total_cost", precision = 18, scale = 2)
    private BigDecimal estimatedTotalCost;

    @Nationalized
    @Column(name = "approval_status", length = 50)
    private String approvalStatus;

}