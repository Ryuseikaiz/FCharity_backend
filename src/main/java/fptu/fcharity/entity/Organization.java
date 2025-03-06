package fptu.fcharity.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_address")
    private Wallet walletAddress;

    @Nationalized
    @Column(name = "organization_description")
    private String organizationDescription;

    @Nationalized
    @Column(name = "pictures")
    private String pictures;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "shutdown_day")
    private Instant shutdownDay;

    @Nationalized
    @Column(name = "organization_status", length = 50)
    private String organizationStatus;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "organization_status", length = 50)
//    private OrganizationStatus organizationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ceo_id")
    private User ceo;

//    public enum OrganizationStatus {
//        PENDING,
//        ACTIVE;
//
//        @JsonCreator
//        public static OrganizationStatus fromString(String value) {
//            return value != null ? OrganizationStatus.valueOf(value.toUpperCase()) : null;
//        }
//    }

}