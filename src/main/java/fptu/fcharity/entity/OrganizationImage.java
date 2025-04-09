package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "organization_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class OrganizationImage {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "organization_image_id", unique = true, updatable = false, nullable = false)
    private UUID organizationImageId;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type")
    private OrganizationImageType imageType;

    public enum OrganizationImageType {
        Avatar, Background, VerificationDocument, General
    }
}
