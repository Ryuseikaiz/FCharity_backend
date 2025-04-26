package fptu.fcharity.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferDto {
    private String account;
    private Long amount;
    private String currency;
    private String transferGroup;
}
