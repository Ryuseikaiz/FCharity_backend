package fptu.fcharity.dto.admindashboard;

import fptu.fcharity.entity.User.UserRole;
import fptu.fcharity.entity.User.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Data
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String avatar;
    private UserRole userRole;
    private UserStatus userStatus;
    private Instant createdDate;
}
