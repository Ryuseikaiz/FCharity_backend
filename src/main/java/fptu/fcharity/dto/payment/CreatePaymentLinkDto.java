package fptu.fcharity.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentLinkDto {
    private String srcAccountId;
    private String desAccountId;
    private String priceId;
}
