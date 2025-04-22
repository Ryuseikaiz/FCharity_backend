package fptu.fcharity.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RequestDto {
    private UUID id;
    private UUID userId;
    private String title;
    private String content;
    private Instant creationDate;
    private String phone;
    private String email;
    private String fullAddress;
    private boolean isEmergency;
    private UUID categoryId;
    private List<UUID> tagIds;
    private String status;
    private List<String> imageUrls;
    private List<String> videoUrls;
    private String reason;
    private String supportType;
}