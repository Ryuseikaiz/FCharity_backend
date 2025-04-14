package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "project_confirmation_requests")
public class ProjectConfirmationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "confirmation_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private HelpRequest request;

    @ColumnDefault("getdate()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("0")
    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @Nationalized
    @Column(name = "confirmation_link", length = 500)
    private String confirmationLink;

    @Nationalized
    @Lob
    @Column(name = "note")
    private String note;

    public ProjectConfirmationRequest(HelpRequest request, Project project,String note) {
        this.request = request;
        this.project = project;
        this.createdAt = Instant.now();
        this.isConfirmed = false;
        this.note = note;
    }
}