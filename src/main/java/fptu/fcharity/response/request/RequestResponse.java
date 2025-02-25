package fptu.fcharity.response.request;

import fptu.fcharity.entity.Category;
import fptu.fcharity.entity.Tag;
import fptu.fcharity.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
public class RequestResponse {
    private UUID id;

    private User user;

    private String title;

    private String content;

    private Instant creationDate;

    private String phone;

    private String email;

    private String location;

    private String attachment;

    private Boolean isEmergency;

    private Category category;

    private Tag tag;

    private String requestStatus;
}
