package fptu.fcharity.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RequestDTO {
    private UUID requestId;
    private UUID userId;
    private String title;
    private String content;
    private LocalDateTime creationDate;
    private String phone;
    private String email;
    private String location;
    private String attachment;
    private boolean isEmergency;
    private UUID categoryId;
    private UUID tagId;
    private String status;
}