package fptu.fcharity.dto.organization;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationRequest;
import fptu.fcharity.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrganizationRequestDTO {
    private UUID organizationRequestId;
    private UserDTO user;
    private OrganizationDTO organization;
    private OrganizationRequest.OrganizationRequestType requestType;
    private OrganizationRequest.OrganizationRequestStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
