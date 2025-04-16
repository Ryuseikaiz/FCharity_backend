package fptu.fcharity.dto.project;

import fptu.fcharity.response.project.ProjectResponse;
import fptu.fcharity.response.project.SpendingItemResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class SpendingDetailDto {
    private UUID id;

    private UUID spendingItemId;

    private UUID projectId;

    private BigDecimal amount;

    private Instant transactionTime;

    private String description;

    private String proofImage;
    public SpendingDetailDto(UUID id, UUID spendingItemId, UUID projectId, BigDecimal amount, Instant transactionTime, String description, String proofImage) {
        this.id = id;
        this.spendingItemId = spendingItemId;
        this.projectId = projectId;
        this.amount = amount;
        this.transactionTime = transactionTime;
        this.description = description;
        this.proofImage = proofImage;
    }
}
