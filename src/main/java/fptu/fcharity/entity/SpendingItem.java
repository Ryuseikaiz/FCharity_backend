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
@Table(name = "spending_items")
public class SpendingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "spending_item_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "spending_plan_id", nullable = false)
    private SpendingPlan spendingPlan;

    @Nationalized
    @Column(name = "item_name")
    private String itemName;

    @Column(name = "estimated_cost", precision = 18, scale = 2)
    private BigDecimal estimatedCost;

    @Nationalized
    @Column(name = "note")
    private String note;

    @ColumnDefault("getdate()")
    @Column(name = "created_date")
    private Instant createdDate;

    @ColumnDefault("getdate()")
    @Column(name = "updated_date")
    private Instant updatedDate;

}