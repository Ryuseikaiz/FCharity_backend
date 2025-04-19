package fptu.fcharity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class PostVoteId implements Serializable {
    @Serial
    private static final long serialVersionUID = 3615165364424463624L;

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    // ✅ Constructor bổ sung
    public PostVoteId(UUID postId, UUID userId) {
        this.postId = postId;
        this.userId = userId;
    }

    // ✅ Constructor mặc định (bắt buộc)
    public PostVoteId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PostVoteId entity = (PostVoteId) o;
        return Objects.equals(this.postId, entity.postId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }
}
