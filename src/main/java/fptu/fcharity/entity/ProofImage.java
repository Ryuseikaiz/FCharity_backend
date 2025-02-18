package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "proof_images")
@Getter
@Setter
public class ProofImage {
    @Id
    @Column(name = "image_id", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private UUID imageId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "image_type", nullable = false)
    private String imageType;

    @ManyToOne
    @JoinColumn(name = "to_project_allocation_id")
    private ToProjectAllocation toProjectAllocation;

    @ManyToOne
    @JoinColumn(name = "to_project_donation_id")
    private ToProjectDonation toProjectDonation;
}