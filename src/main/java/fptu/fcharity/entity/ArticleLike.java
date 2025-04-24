package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "article_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ArticleLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "article_like_id")
    private UUID articleLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", referencedColumnName = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
