package fptu.fcharity.repository.manage.post;

import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.PostReport;
import fptu.fcharity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, UUID> {
    boolean existsByPostAndReporter(Post post, User reporter);
}