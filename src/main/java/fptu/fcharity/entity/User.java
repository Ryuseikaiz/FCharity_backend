package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column
    private String address;

    @Column
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;


    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expires_at")
    private LocalDateTime verificationCodeExpiresAt;

    // Constructor for creating an unverified user
    public User(String fullName, String email, String password, String phoneNumber, String address, String avatar, String userRole, LocalDateTime createdDate, UserStatus userStatus) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.avatar = avatar;
        this.userRole = UserRole.User;
        this.createdDate = createdDate;
        this.userStatus = userStatus;
    }

    // Constructor for creating a user with username, email, and password
    public User(String username, String email, String password) {
        this.fullName = username;
        this.email = email;
        this.password = password;
        this.createdDate = LocalDateTime.now();
        this.userStatus = UserStatus.Unverified;
        this.userRole = UserRole.User;
    }

    // Default constructor
    public User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userStatus == UserStatus.Verified;
    }

    public void setEnabled(boolean enabled) {
        this.userStatus = enabled ? UserStatus.Verified : UserStatus.Unverified;
    }

    @Override
    public String getUsername() {
        return email;
    }

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