package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "requests")
@Getter
@Setter
public class Request {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name="request_id", unique = true, updatable = false, nullable = false)
    private UUID requestId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(length = 15)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String location;

    @Column(name="attachment")
    private String attachment;

    @Column(name = "is_emergency", nullable = false)
    private boolean isEmergency;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryId;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tagId;

    @Column(nullable = false)
    private String status;

    public Request() {
    }

    public Request(UUID requestId, User user, String title, String content, LocalDateTime creationDate, String phone, String email, String location, String attachment, boolean isEmergency, Category categoryId, Tag tagId, String status) {
        this.requestId = requestId;
        this.user = user;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.attachment = attachment;
        this.isEmergency = isEmergency;
        this.categoryId = categoryId;
        this.tagId = tagId;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId=" + requestId +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", creationDate=" + creationDate +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", location='" + location + '\'' +
                ", attachment='" + attachment + '\'' +
                ", isEmergency=" + isEmergency +
                ", categoryId=" + categoryId +
                ", tagId=" + tagId +
                ", status='" + status + '\'' +
                '}';
    }
}