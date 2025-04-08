package fptu.fcharity.dto.organization;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WalletDTO {
    private UUID id;
    private int balance;
}
