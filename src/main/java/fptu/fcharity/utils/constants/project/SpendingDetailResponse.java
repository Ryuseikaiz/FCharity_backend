package fptu.fcharity.utils.constants.project;

import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.SpendingDetail;
import fptu.fcharity.entity.SpendingItem;
import fptu.fcharity.response.project.ProjectResponse;
import fptu.fcharity.response.project.SpendingItemResponse;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class SpendingDetailResponse {
    private UUID id;

    private SpendingItemResponse spendingItem;

    private ProjectResponse project;

    private BigDecimal amount;

    private Instant transactionTime;

    private String description;

    private String proofImage;
    public SpendingDetailResponse(SpendingDetail spendingDetail) {
        this.id = spendingDetail.getId();
        this.spendingItem = new SpendingItemResponse(spendingDetail.getSpendingItem());
        this.project = new ProjectResponse(spendingDetail.getProject());
        this.amount = spendingDetail.getAmount();
        this.transactionTime = spendingDetail.getTransactionTime();
        this.description = spendingDetail.getDescription();
        this.proofImage = spendingDetail.getProofImage();
    }
}
