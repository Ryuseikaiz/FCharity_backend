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
public class ExtraFundRequestResponse {
    private UUID id;
    private ProjectResponse project;
    private BigDecimal amount;
    private String proofImage;
    private String reason;
    private String status;
    private Instant createdDate;
    private Instant updatedDate;
    private OrganizationDTO organization;
    public ExtraFundRequestResponse(ProjectExtraFundRequest request){
        this.id = request.getId();
        this.project = new ProjectResponse(request.getProject());
        this.amount = request.getAmount();
        this.proofImage = request.getProofImage();
        this.reason = request.getReason();
        this.status = request.getStatus();
        this.createdDate = request.getCreatedDate();
        this.updatedDate = request.getUpdatedDate();
    }
}
