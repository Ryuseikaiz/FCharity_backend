package fptu.fcharity.dto.post;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentDTO {

    private UUID commentId;
    private UUID postId;
    private UUID userId;
    private String fullName;
    private String avatar;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID parentCommentId;
    private long vote;

    // Constructors
    public CommentDTO() {
    }

    public CommentDTO(UUID commentId, UUID postId, UUID userId, String fullName, String avatar,
                      String content, LocalDateTime createdAt, LocalDateTime updatedAt,
                      UUID parentCommentId, long vote) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.fullName = fullName;
        this.avatar = avatar;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.parentCommentId = parentCommentId;
        this.vote = vote;
    }

    // Constructor from Comment entity
    public CommentDTO(Comment comment) {
        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.userId = comment.getUser().getUserId();
        this.fullName = comment.getUser().getFullName();
        this.avatar = comment.getUser().getAvatar();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.parentCommentId = comment.getParentCommentId();
        this.vote = comment.getVote();
    }

    // Getters and Setters
    public UUID getCommentId() {
        return commentId;
    }

    public void setCommentId(UUID commentId) {
        this.commentId = commentId;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(UUID parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public long getVote() {
        return vote;
    }

    public void setVote(long vote) {
        this.vote = vote;
    }

    @Override
    public String toString() {
        return "PostCommentDTO{" +
                "commentId=" + commentId +
                ", postId=" + postId +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", parentCommentId=" + parentCommentId +
                ", vote=" + vote +
                '}';
    }
}