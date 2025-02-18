package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "requests")
@Getter
@Setter
public class Request {
    @Id
    @Column(name = "request_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID requestId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(length = 15)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String location;

    @Column
    private String attachment;

    @Column(name = "is_emergency", nullable = false)
    private boolean isEmergency;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Column(nullable = false)
    private String status;
}