package fptu.fcharity.response.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccountLinkResponse {
    private String url;

    public CreateAccountLinkResponse(String url) {
        this.url = url;
    }
}