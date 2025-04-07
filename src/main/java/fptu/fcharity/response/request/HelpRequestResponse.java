package fptu.fcharity.response.request;

import fptu.fcharity.entity.Category;
import fptu.fcharity.entity.HelpRequest;
import fptu.fcharity.entity.User;
import fptu.fcharity.response.authentication.UserResponse;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class HelpRequestResponse {
    private UUID id;

    private UserResponse user;

    private String title;

    private String content;

    private Instant creationDate;

    private String phone;

    private String email;

    private String location;

    private Boolean isEmergency;

    private Category category;

    private String status;

    private String reason;

    public HelpRequestResponse(HelpRequest helpRequest) {
        this.id = helpRequest.getId();
        this.user = new UserResponse(helpRequest.getUser());
        this.title = helpRequest.getTitle();
        this.content = helpRequest.getContent();
        this.creationDate = helpRequest.getCreationDate();
        this.phone = helpRequest.getPhone();
        this.email = helpRequest.getEmail();
        this.location = helpRequest.getLocation();
        this.isEmergency = helpRequest.getIsEmergency();
        this.category = helpRequest.getCategory();
        this.status = helpRequest.getStatus();
        this.reason = helpRequest.getReason();
    }
}
