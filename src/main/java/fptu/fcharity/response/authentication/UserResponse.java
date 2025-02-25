package fptu.fcharity.response.authentication;

import fptu.fcharity.entity.User;
import fptu.fcharity.entity.Wallet;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
public class UserResponse {
    private UUID userId;

    private String fullName;

    private String email;

    private String password;

    private String phoneNumber;

    private String address;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private User.UserRole userRole;


    private Instant createdDate;

    @Enumerated(EnumType.STRING)
    private User.UserStatus userStatus;

    private String verificationCode;

    private Wallet walletAddress;
    public enum UserStatus {
        Unverified,
        Verified,
        Banned
    }
    public enum UserRole {
        Admin,
        User,
    }
}
