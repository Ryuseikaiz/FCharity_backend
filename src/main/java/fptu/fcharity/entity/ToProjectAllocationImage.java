package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "to_project_allocation_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class ToProjectAllocationImage {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "image_id", unique = true, updatable = false, nullable = false)
    private UUID imageId;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "to_project_allocation_id", referencedColumnName = "allocation_id")
    private ToProjectAllocation toProjectAllocation;
}
