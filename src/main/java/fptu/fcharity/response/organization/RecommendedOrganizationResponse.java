package fptu.fcharity.response.organization;

import fptu.fcharity.utils.constants.OrganizationStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RecommendedOrganizationResponse {
    private UUID organizationId;
    private String organizationName;
    private String organizationDescription;
    private String status;

    private String backgroundUrl;
    private int totalMembers;
    private int totalProjects;
    private int totalCompletedProjects;
}
