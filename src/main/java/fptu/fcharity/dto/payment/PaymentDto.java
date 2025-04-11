package fptu.fcharity.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentDto {
    private String itemContent;
    private String paymentContent;
    private int amount;
    private UUID userId;
}
