package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "object_images")
@Getter
@Setter
public class ObjectImage {
    @Id
    @Column(name = "image_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID imageId;

    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "phase_id")
    private Timeline phase;
}