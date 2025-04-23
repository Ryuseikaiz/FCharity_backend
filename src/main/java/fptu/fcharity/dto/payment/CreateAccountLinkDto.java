package fptu.fcharity.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccountLinkDto {
    private String account;

    public CreateAccountLinkDto(String account) {
        this.account = account;
    }

    public String getAccount() {
        return this.account;
    }
}
