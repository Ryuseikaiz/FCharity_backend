package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "to_project_donations")
public class ToProjectDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "donation_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Nationalized
    @Column(name = "donation_status", length = 50)
    private String donationStatus;

    @Column(name = "donation_time")
    private Instant donationTime;

    @Nationalized
    @Column(name = "message")
    private String message;

    @Nationalized
    @Column(name = "order_code")
    private int orderCode;

}