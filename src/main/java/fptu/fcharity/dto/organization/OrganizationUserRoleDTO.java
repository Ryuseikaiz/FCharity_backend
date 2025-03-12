package fptu.fcharity.dto.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrganizationUserRoleDTO {
    private UUID userId;
    private UUID organizationId;
    private UUID roleId;
}
