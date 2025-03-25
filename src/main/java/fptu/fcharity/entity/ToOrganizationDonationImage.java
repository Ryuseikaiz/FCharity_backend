package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "to_organization_donation_images")
public class ToOrganizationDonationImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "image_id", nullable = false, length = 36)
    private String imageId;

    @Nationalized
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_organization_donation_id")
    private ToOrganizationDonation toOrganizationDonation;

}