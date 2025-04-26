package fptu.fcharity.service.manage.organization.article;

import fptu.fcharity.dto.organization.ArticleDTO;
import fptu.fcharity.dto.organization.ArticleLikeDTO;
import java.util.List;
import java.util.UUID;

public interface ArticleService {
    List<ArticleDTO> getAllArticles();
    ArticleDTO getArticleById(UUID articleId);
    List<ArticleDTO> getArticlesByOrganizationId(UUID organizationId);
    ArticleDTO createArticle(ArticleDTO articleDTO);
    ArticleDTO updateArticle(ArticleDTO articleDTO);
    void deleteArticle(UUID articleId);
    List<ArticleLikeDTO> getAllArticleLikes();
    List<ArticleLikeDTO> getArticleLikesByArticleId(UUID articleId);
    void likeArticle(UUID articleId, UUID userId);
    void unlikeArticle(UUID articleId, UUID userId);
}
