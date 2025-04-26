package fptu.fcharity.dto.organization;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventEmailAccessDTO {
    private UUID eventEmailAccessId;
    private OrganizationEventDTO organizationEvent;
    private String email;
    private fptu.fcharity.entity.EventEmailAccess.AccessType accessType;

}
