package fptu.fcharity.service.manage.organization.article;

import fptu.fcharity.dto.organization.ArticleDTO;
import fptu.fcharity.dto.organization.ArticleLikeDTO;
import fptu.fcharity.entity.Article;
import fptu.fcharity.entity.ArticleLike;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.organization.ArticleLikeRepository;
import fptu.fcharity.repository.manage.organization.ArticleRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.organization.ArticleLikeMapper;
import fptu.fcharity.utils.mapper.organization.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;
    private final ArticleLikeMapper articleLikeMapper;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, ArticleLikeRepository articleLikeRepository, ArticleMapper articleMapper, ArticleLikeMapper articleLikeMapper, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.articleLikeRepository = articleLikeRepository;
        this.articleMapper = articleMapper;
        this.articleLikeMapper = articleLikeMapper;
        this.userRepository = userRepository;
    }

    public List<ArticleDTO> getAllArticles() {
        return articleRepository
                .findAll().stream()
                .map(articleMapper::toDTO).collect(Collectors.toList());
    }

    public ArticleDTO getArticleById(UUID articleId) {
        return articleMapper.toDTO(
                articleRepository.findById(articleId)
                        .orElseThrow(() -> new ApiRequestException("Article not found")
                        )
        );
    }

    public List<ArticleDTO> getArticlesByOrganizationId(UUID organizationId) {
        return articleRepository.findArticleByOrganizationOrganizationId(organizationId)
                .stream()
                .map(articleMapper::toDTO)
                .collect(Collectors.toList());
    }

    //TODO: check role before create
    @Transactional
    public ArticleDTO createArticle(ArticleDTO articleDTO) {
        Article article = articleMapper.toEntity(articleDTO);
        article.setCreatedAt(Instant.now());
        article.setUpdatedAt(Instant.now());

        System.out.println("Before saving article: ⚓⚓ " + article);
        return articleMapper.toDTO(articleRepository.save(article));
    }

    //TODO: check role before create
    @Transactional
    public ArticleDTO updateArticle(ArticleDTO articleDTO) {
        Article article = articleMapper.toEntity(articleDTO);
        article.setUpdatedAt(Instant.now());

        return articleMapper.toDTO(articleRepository.save(article));
    }

    //TODO: check role before create
    @Transactional
    public void deleteArticle(UUID articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ApiRequestException("Article not found"));
        articleRepository.delete(article);
    }

    public List<ArticleLikeDTO> getAllArticleLikes() {
        return articleLikeRepository
                .findAll().stream()
                .map(articleLikeMapper::toDTO).collect(Collectors.toList());
    }

    public List<ArticleLikeDTO> getArticleLikesByArticleId(UUID articleId) {
        return articleLikeRepository.findArticleLikesByArticleArticleId(articleId)
                .stream()
                .map(articleLikeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void likeArticle(UUID articleId, UUID userId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ApiRequestException("Article not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiRequestException("User not found"));

        ArticleLike newArticleLike = new ArticleLike();
        newArticleLike.setArticle(article);
        newArticleLike.setUser(user);
        newArticleLike.setCreatedAt(Instant.now());

        articleLikeRepository.save(newArticleLike);
    }

    @Transactional
    public void unlikeArticle(UUID articleId, UUID userId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ApiRequestException("Article not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiRequestException("User not found"));

        ArticleLike articleLike = articleLikeRepository.findArticleLikeByArticleArticleIdAndUserId(articleId, user.getId());
        if (articleLike != null) {
            articleLikeRepository.delete(articleLike);
        } else
            throw new ApiRequestException("Article like not found");
    }
}
