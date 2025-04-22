package fptu.fcharity.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PaymentDto {
    private String itemContent;
    private String paymentContent;
    private int amount;
    private UUID objectId;
    private UUID userId;
    private String objectType;
    private String returnUrl;
}
