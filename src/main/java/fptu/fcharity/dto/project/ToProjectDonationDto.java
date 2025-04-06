package fptu.fcharity.dto.project;

import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ToProjectDonationDto {
    private UUID projectId;
    private BigDecimal amount;
    private UUID userId;
    private String donationStatus;
    private String message;
}
