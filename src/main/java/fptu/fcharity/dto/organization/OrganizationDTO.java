package fptu.fcharity.dto.organization;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationDTO {
    private String organizationId;
    private String organizationName;
    private String email;
    private String phoneNumber;
    private String address;
    private String organizationDescription;
    private String pictures;
    private String description;
    private String organizationStatus;
}
