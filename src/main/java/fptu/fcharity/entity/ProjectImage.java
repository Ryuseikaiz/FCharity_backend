package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "project_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class ProjectImage {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "project_image_id", unique = true, updatable = false, nullable = false)
    private UUID projectImageId;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    private Project project;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_type")
    private String imageType;
}
