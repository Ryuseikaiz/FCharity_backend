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
@Table(name = "to_project_allocations")
public class ToProjectAllocation {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "allocation_id", nullable = false)
    private UUID id;

    @Column(name = "organization_id")
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Nationalized
    @Column(name = "allocation_status", length = 50)
    private String allocationStatus;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Nationalized
    @Column(name = "message")
    private String message;

    @Column(name = "allocation_time")
    private Instant allocationTime;

}