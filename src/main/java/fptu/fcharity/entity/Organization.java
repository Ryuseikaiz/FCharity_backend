package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@Setter
public class Organization {
    @Id
    @Column(name="organization_id", unique = true, updatable = false, nullable = false)
    private UUID organizationId;

    @Column(name="organization_name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="address")
    private String address;

    @Column(name="wallet_address")
    private String walletAddress;

    @Column(name="organization_description")
    private String organizationDescription;

    @Column(name="pictures")
    private String picture;

    @Column(name="start_time")
    private Date startTime;

    @Column(name="shutdown_day")
    private Date shutdownDay;

    @Column(name="organization_status")
    private String organizationStatus;

    @Column(name="ceo_id")
    private UUID ceoId;

    public Organization() {
    }

    public Organization(UUID organizationId, String name, String email, String phoneNumber, String address, String walletAddress, String organizationDescription, String picture, Date startTime, Date shutdownDay, String organizationStatus, UUID ceoId) {
        this.organizationId = organizationId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.walletAddress = walletAddress;
        this.organizationDescription = organizationDescription;
        this.picture = picture;
        this.startTime = startTime;
        this.shutdownDay = shutdownDay;
        this.organizationStatus = organizationStatus;
        this.ceoId = ceoId;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "organizationId=" + organizationId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", walletAddress='" + walletAddress + '\'' +
                ", organizationDescription='" + organizationDescription + '\'' +
                ", picture='" + picture + '\'' +
                ", startTime=" + startTime +
                ", shutdownDay=" + shutdownDay +
                ", organizationStatus='" + organizationStatus + '\'' +
                ", ceoId='" + ceoId + '\'' +
                '}';
    }
}
