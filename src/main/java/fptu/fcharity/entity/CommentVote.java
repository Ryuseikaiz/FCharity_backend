package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "comment_votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentVote {
    @EmbeddedId
    private CommentVoteId id;

    @Column(name = "is_upvote", nullable = false)
    private Boolean isUpvote;

    @ManyToOne
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int vote; // +1 or -1
}