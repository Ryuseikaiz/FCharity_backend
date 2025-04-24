package fptu.fcharity.dto.organization;

import fptu.fcharity.utils.constants.OrganizationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrganizationRankingDTO {
    private UUID organizationId;
    private String organizationName;
    private String backgroundUrl;
    private String email;

    private int numberOfMembers;
    private int numberOfProjects;
    private BigDecimal totalFunding;
    private String organizationStatus;
}
