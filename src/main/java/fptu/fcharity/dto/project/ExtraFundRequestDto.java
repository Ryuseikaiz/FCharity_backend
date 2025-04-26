package fptu.fcharity.response.project;

import fptu.fcharity.dto.organization.OrganizationDTO;
import fptu.fcharity.entity.ProjectExtraFundRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class ExtraFundRequestDto {
    private UUID id;
    private UUID projectId;
    private BigDecimal amount;
    private String proofImage;
    private String reason;
    private String status;
    private Instant createdDate;
    private Instant updatedDate;
    private UUID organizationId;

}
