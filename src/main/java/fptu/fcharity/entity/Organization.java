package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Organization {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name="organization_id", unique = true, updatable = false, nullable = false)
    @ColumnDefault("newid()")
    private UUID organizationId;

    @Nationalized
    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Nationalized
    @Column(name = "email", nullable = false)
    private String email;

    @Nationalized
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Nationalized
    @Column(name="address")
    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_address")
    private Wallet walletAddress;

    @Nationalized
    @Column(name="organization_description")
    private String organizationDescription;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "shutdown_day")
    private Instant shutdownDay;

    @Nationalized
    @Column(name="organization_status", length = 50)
    private String organizationStatus;

    // TODO: ceo id -> object
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ceo_id")
    private User ceo;

    @Column(name = "reason")
    private String reason;

    @Column(name = "advice")
    private String advice;
}

