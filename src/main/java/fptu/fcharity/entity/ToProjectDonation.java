package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "to_project_donations")
@Getter
@Setter
public class ToProjectDonation {
    @Id
    @Column(name = "donation_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID donationId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "donation_status", nullable = false)
    private String donationStatus;

    @Column(name = "donation_time", nullable = false)
    private LocalDateTime donationTime;

    @Column(name = "message")
    private String message;
}