package fptu.fcharity.response.authentication;

import fptu.fcharity.entity.User;
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
    private UUID id;

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

    private Instant verificationCodeExpiresAt;

    public enum UserStatus {
        Unverified,
        Verified,
        Banned
    }
    public enum UserRole {
        Admin,
        Manager,
        User,
        Leader
    }
    public UserResponse() {
    }
    public UserResponse(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.avatar = user.getAvatar();
        this.userRole = user.getUserRole();
        this.createdDate = user.getCreatedDate();
        this.userStatus = user.getUserStatus();
        this.verificationCode = user.getVerificationCode();
        this.verificationCodeExpiresAt = user.getVerificationCodeExpiresAt();
    }
}
