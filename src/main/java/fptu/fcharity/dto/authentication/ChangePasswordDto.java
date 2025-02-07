package fptu.fcharity.dto.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
    private String email;
    private String oldPassword;
    private String newPassword;
}
