package fptu.fcharity.dto.organization;

import fptu.fcharity.entity.Organization;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrganizationDTO {
    private UUID organizationId;
    private String organizationName;
    private String email;
    private String phoneNumber;
    private String address;
    private WalletDTO walletAddress;
    private String organizationDescription;
    private Instant startTime;
    private Instant shutdownDay;
    private String organizationStatus;
    private String backgroundUrl;
    private UserDTO ceo;
    private String reason;
    private String advice;
}