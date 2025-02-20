package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "to_project_allocations")
@Getter
@Setter
public class ToProjectAllocation {
    @Id
    @Column(name = "allocation_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID allocationId;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "allocation_status", nullable = false)
    private String allocationStatus;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "message")
    private String message;

    @Column(name = "allocation_time", nullable = false)
    private LocalDateTime allocationTime;
}