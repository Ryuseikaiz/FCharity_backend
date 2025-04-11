package fptu.fcharity.dto.organization;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WalletDTO {
    private UUID id;
    private BigDecimal balance;
}
