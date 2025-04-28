package fptu.fcharity.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectNeedDonateDto {
    private UUID projectId;
    private Instant plannedStartTime;
    private BigDecimal totalDonations;
    private BigDecimal estimateCost;

}
