package fptu.fcharity.dto.organization;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ToOrganizationDonationDTO {
    private UUID id;
    private UserDTO user;
    private OrganizationDTO organization;
    private BigDecimal amount;
    private String donationStatus;
    private Instant donationTime;
    private String message;
    private int orderCode;
}
