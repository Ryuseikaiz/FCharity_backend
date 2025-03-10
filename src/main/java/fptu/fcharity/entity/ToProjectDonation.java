package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "to_project_donations")
public class ToProjectDonation {
    @Id
    @Column(name = "donation_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
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

}