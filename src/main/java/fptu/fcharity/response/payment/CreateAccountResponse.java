package fptu.fcharity.response.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccountResponse {
    private String account;

    public CreateAccountResponse(String account) {
        this.account = account;
    }
}
