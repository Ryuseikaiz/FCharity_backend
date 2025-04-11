package fptu.fcharity.response.project;

import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.ToProjectDonation;
import fptu.fcharity.entity.User;
import fptu.fcharity.response.authentication.UserResponse;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ToProjectDonationResponse {
    private UUID id;

    private UUID projectId;

    private BigDecimal amount;

    private UserResponse user;

    private String donationStatus;

    private Instant donationTime;

    private String message;
    private int orderCode;
    public ToProjectDonationResponse(ToProjectDonation t){
        this.amount = t.getAmount();
        this.id = t.getId();
        this.projectId = t.getProject().getId();
        this.user = new UserResponse(t.getUser());
        this.donationStatus = t.getDonationStatus();
        this.donationTime = t.getDonationTime();
        this.message = t.getMessage();
        this.orderCode = t.getOrderCode();
    }
}
