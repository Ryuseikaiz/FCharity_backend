package fptu.fcharity.dto.request;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
public class InviteJoinRequestDto {
    private UUID inviteJoinRequestId;
    private UUID userId;
    private UUID organizationId;
    private String title;
    private String content;
    private String cvLocation;
    private String requestType;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
