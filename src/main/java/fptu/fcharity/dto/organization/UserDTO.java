package fptu.fcharity.dto.organization;

import fptu.fcharity.entity.User;
import fptu.fcharity.entity.Wallet;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String avatar;
    private User.UserRole userRole;
    private Instant createdDate;
    private User.UserStatus userStatus;
    private String verificationCode;
    private Instant verificationCodeExpiresAt;
    private String reason;
}
