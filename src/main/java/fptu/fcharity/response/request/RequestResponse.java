package fptu.fcharity.response.request;

import fptu.fcharity.entity.Category;
import fptu.fcharity.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

public class RequestResponse {
    private UUID id;
    private User user;

    private String title;

    private String content;

    private Instant creationDate;

    private String phone;

    private String email;

    private String location;

    private Boolean isEmergency;

    private Category category;

    private String status;

}
