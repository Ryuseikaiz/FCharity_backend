package fptu.fcharity.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePayoutDto {
    private String accountId;
    private Long amount;
    private String currency;
    private String description;

}
