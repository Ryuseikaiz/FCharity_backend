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
    @JoinColumn(name = "phase_id")
    private Timeline phase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "help_request_id")
    private HelpRequest helpRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

}