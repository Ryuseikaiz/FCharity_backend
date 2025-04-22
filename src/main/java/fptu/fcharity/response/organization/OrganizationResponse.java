package fptu.fcharity.response.organization;

import fptu.fcharity.dto.organization.UserDTO;
import fptu.fcharity.dto.organization.WalletDTO;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrganizationResponse {
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
    private UserDTO ceo;
    private String reason;
    private String advice;
    private String avatarUrl;
    private String backgroundUrl;
}


