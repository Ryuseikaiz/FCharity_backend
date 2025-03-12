package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "object_attachments")
public class ObjectAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "image_id", nullable = false)
    private UUID id;

    @Nationalized
    @Column(name = "url")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_id")
    private Timeline phase;

    public ObjectAttachment(UUID id, String url, UUID objectId, String objectType) {
        this.id = id;
        this.url = url;

        switch (objectType) {
            case "REQUEST":
                this.request = new Request(objectId); // Creates a new Request but does not fetch existing entity
                break;
            default:
                throw new IllegalArgumentException("Invalid object type: " + objectType);
        }
    }

    public ObjectAttachment() {
    }
}