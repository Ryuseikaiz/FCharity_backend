package fptu.fcharity.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateProfileDto {
    private String fullName;
    private String phoneNumber;
    private String fullAddress;
    private String avatar;

}
