package fptu.fcharity.entity;

import fptu.fcharity.utils.constants.PostStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "post_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Nationalized
    @Column(name = "title")
    private String title;

    @Nationalized
    @Column(name = "content")
    private String content;

    @Column(name = "vote")
    private Integer vote;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Nationalized
    @Column(name = "post_status", length = 50)
    private String postStatus;

    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
    public Post() {
    }
    public Post(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.vote = 0;
        this.postStatus = PostStatus.ACTIVE;
    }
}