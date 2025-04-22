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
    // Getter và Setter
    @Setter
    @Getter
    @Column(nullable = false)
    @ColumnDefault("'PENDING'") // Đảm bảo giá trị mặc định trong DB cũng là PENDING
    private String postStatus = PostStatus.PENDING;
    @ManyToOne(fetch = FetchType.EAGER)
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

    @Column(name = "reason")
    private String reason;


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
        this.postStatus = PostStatus.PENDING; // Đảm bảo khi tạo luôn là PENDING
        this.createdAt  = Instant.now();
    }

}

