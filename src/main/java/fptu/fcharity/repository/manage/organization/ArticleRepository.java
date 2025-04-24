package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.Article;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {
    @EntityGraph(attributePaths = {
            "organization",
            "organization.walletAddress",
            "organization.ceo",
            "author",
    })
    List<Article> findArticleByOrganizationOrganizationId(UUID organizationId);
}
