package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "post_votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class PostVote {

    @EmbeddedId
    private PostVoteId id;

//    @ManyToOne
//    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
//    private Post post;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
//    private User user;

    @Column(name = "vote")
    private int vote;

    @Column(name = "created_at")
    private Instant created_at;

    @Column(name = "updated_at")
    private Instant updated_at;

}
