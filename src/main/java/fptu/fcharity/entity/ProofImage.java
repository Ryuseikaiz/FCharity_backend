package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "proof_images")
public class ProofImage {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "image_id", nullable = false, length = 36)
    private String imageId;

    @Nationalized
    @Column(name = "image_url")
    private String imageUrl;

    @Nationalized
    @Column(name = "image_type", length = 20)
    private String imageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_project_allocation_id")
    private ToProjectAllocation toProjectAllocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_project_donation_id")
    private ToProjectDonation toProjectDonation;

}