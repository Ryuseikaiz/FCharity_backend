package fptu.fcharity.dto.organization;

import fptu.fcharity.dto.project.ProjectDto;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrganizationTransactionHistoryDTO {
    private UUID id;
    private OrganizationDTO organization;
    private ProjectDto project;
    private String transactionStatus;
    private BigDecimal amount;
    private String message;
    private Instant transactionTime;
    private String transactionType;
}
