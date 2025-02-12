package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="request")
@Getter
@Setter
public class Request {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name="request_id", unique = true, updatable = false, nullable = false)
    private UUID requestId;

    @Column(name="user_id")
    private UUID userId;

    @Column(name="title")
    private String requestTitle;

    @Column(name="content")
    private String content;

    @Column(name="creation_date")
    private Date creationDate;

    @Column(name="phone")
    private String phone;

    @Column(name="email")
    private String email;

    @Column(name="location")
    private String location;

    @Column(name="attachment")
    private String attachment;

    @Column(name="is_emergency")
    private boolean isEmergency;

    @Column(name="category_id")
    private UUID categoryId;

    @Column(name="tag_id")
    private UUID tagId;

    public Request() {
    }

    public Request(UUID requestId, UUID userId, String requestTitle, String content, Date creationDate, String phone, String email, String location, String attachment, boolean isEmergency, UUID categoryId, UUID tagId) {
        this.requestId = requestId;
        this.userId = userId;
        this.requestTitle = requestTitle;
        this.content = content;
        this.creationDate = creationDate;
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.attachment = attachment;
        this.isEmergency = isEmergency;
        this.categoryId = categoryId;
        this.tagId = tagId;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId=" + requestId +
                ", userId=" + userId +
                ", requestTitle='" + requestTitle + '\'' +
                ", content='" + content + '\'' +
                ", creationDate=" + creationDate +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", location='" + location + '\'' +
                ", attachment='" + attachment + '\'' +
                ", isEmergency=" + isEmergency +
                ", categoryId=" + categoryId +
                ", tagId=" + tagId +
                '}';
    }
}
