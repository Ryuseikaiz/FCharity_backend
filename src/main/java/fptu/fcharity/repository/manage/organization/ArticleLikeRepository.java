package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.ArticleLike;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, UUID> {
    @EntityGraph(attributePaths = {
            "article",
            "article.organization",
            "article.organization.walletAddress",
            "article.organization.ceo",
            "article.author",
            "user",
    })
    List<ArticleLike> findArticleLikesByArticleArticleId(UUID articleId);

    @EntityGraph(attributePaths = {
            "article",
            "article.organization",
            "article.organization.walletAddress",
            "article.organization.ceo",
            "article.author",
            "user",
    })
    ArticleLike findArticleLikeByArticleArticleIdAndUserId(UUID articleId, UUID userId);
}
