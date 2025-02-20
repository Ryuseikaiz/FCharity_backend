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
    @GeneratedValue(generator = "UUID")
    @Column(name="organization_id", unique = true, updatable = false, nullable = false)
    private UUID organizationId;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name="address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "wallet_address")
    private Wallet walletAddress;

    @Column(name="organization_description")
    private String organizationDescription;

    @Column(name = "pictures")
    private String pictures;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "shutdown_day")
    private LocalDateTime shutdownDay;

    @Column(name="organization_status")
    private String organizationStatus;

    @ManyToOne
    @JoinColumn(name = "ceo_id")
    private User ceo;

    public Organization() {
    }

    public Organization(UUID organizationId, String organizationName, String email, String phoneNumber, String address, Wallet walletAddress, String organizationDescription, String pictures, LocalDateTime startTime, LocalDateTime shutdownDay, String organizationStatus, User ceo) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.walletAddress = walletAddress;
        this.organizationDescription = organizationDescription;
        this.pictures = pictures;
        this.startTime = startTime;
        this.shutdownDay = shutdownDay;
        this.organizationStatus = organizationStatus;
        this.ceo = ceo;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", walletAddress=" + walletAddress +
                ", organizationDescription='" + organizationDescription + '\'' +
                ", pictures='" + pictures + '\'' +
                ", startTime=" + startTime +
                ", shutdownDay=" + shutdownDay +
                ", organizationStatus='" + organizationStatus + '\'' +
                ", ceo=" + ceo +
                '}';
    }
}

