package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.ArticleDTO;
import fptu.fcharity.dto.organization.ArticleLikeDTO;
import fptu.fcharity.dto.organization.UserDTO;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.service.manage.organization.article.ArticleService;
import fptu.fcharity.service.manage.organization.article.ArticleServiceImpl;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.organization.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public ArticleController(ArticleServiceImpl articleService, UserRepository userRepository, UserMapper userMapper) {
        this.articleService = articleService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable UUID articleId) {
        return ResponseEntity.ok(articleService.getArticleById(articleId));
    }

    @GetMapping("/organizations/{organizationId}")
    public ResponseEntity<List<ArticleDTO>> getArticlesByOrganizationId(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(articleService.getArticlesByOrganizationId(organizationId));
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody ArticleDTO articleDTO) {
        System.out.println("Create Article 🛸🛸" + articleDTO);
        return ResponseEntity.ok(articleService.createArticle(articleDTO));
    }

    @PutMapping
    public ResponseEntity<ArticleDTO> updateArticle(@RequestBody ArticleDTO articleDTO) {
        return ResponseEntity.ok(articleService.updateArticle(articleDTO));
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID articleId) {
        articleService.deleteArticle(articleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/likes")
    public ResponseEntity<List<ArticleLikeDTO>> getAllArticleLikes() {
        return ResponseEntity.ok(articleService.getAllArticleLikes());
    }

    @GetMapping("/likes/{articleId}")
    public ResponseEntity<List<ArticleLikeDTO>> getArticleLikesByArticleId(@PathVariable UUID articleId) {
        return ResponseEntity.ok(articleService.getArticleLikesByArticleId(articleId));
    }

    @PostMapping("/{articleId}/{userId}/like")
    public ResponseEntity<Void> likeArticle(@PathVariable UUID articleId, @PathVariable UUID userId) {
        articleService.likeArticle(articleId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{articleId}/{userId}/unlike")
    public ResponseEntity<Void> unlikeArticle(@PathVariable UUID articleId, @PathVariable UUID userId) {
        articleService.unlikeArticle(articleId, userId);
        return ResponseEntity.ok().build();
    }

    // Lấy thông tin tác giả đang tạo bài
    @GetMapping("/author")
    public ResponseEntity<UserDTO> getAuthor() {
        User author = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new ApiRequestException("Author not found!"));
        return ResponseEntity.ok(userMapper.toDTO(author));
    }
}
