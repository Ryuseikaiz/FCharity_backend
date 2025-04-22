package fptu.fcharity.dto.request;

import fptu.fcharity.entity.OrganizationRequest;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationRequestDTO {
    private UUID inviteJoinRequestId;
    private UUID userId;
    private UUID organizationId;
    private String title;
    private String content;
    private String cvLocation;
    private OrganizationRequest.OrganizationRequestType requestType;
    private OrganizationRequest.OrganizationRequestStatus status;
    private Date createdAt;
    private Date updatedAt;
}
