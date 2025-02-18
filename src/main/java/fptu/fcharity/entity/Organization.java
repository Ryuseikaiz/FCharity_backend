package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@Setter
public class Organization {
    @Id
    @Column(name = "organization_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID organizationId;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "wallet_address")
    private Wallet wallet;

    @Column(name = "organization_description")
    private String organizationDescription;

    @Column(name = "pictures")
    private String pictures;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "shutdown_day")
    private LocalDateTime shutdownDay;

    @Column(name = "organization_status")
    private String organizationStatus;

    @ManyToOne
    @JoinColumn(name = "ceo_id")
    private User ceo;
}