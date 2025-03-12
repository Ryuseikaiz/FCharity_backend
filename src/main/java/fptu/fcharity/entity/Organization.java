package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "organization_id", nullable = false)
    private UUID id;

    @Nationalized
    @Column(name = "organization_name")
    private String organizationName;

    @Nationalized
    @Column(name = "email")
    private String email;

    @Nationalized
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Nationalized
    @Column(name = "address")
    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_address")
    private Wallet walletAddress;

    @Nationalized
    @Column(name = "organization_description")
    private String organizationDescription;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "shutdown_day")
    private Instant shutdownDay;

    @Nationalized
    @Column(name = "organization_status", length = 50)
    private String organizationStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ceo_id")
    private User ceo;
}