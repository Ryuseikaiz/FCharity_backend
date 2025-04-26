package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "wallets")
@ToString
public class Wallet {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "wallet_id", updatable = false, nullable = false)
    private UUID id;

    @Nationalized
    @Column(name = "balance", precision = 38, scale = 2)
    private BigDecimal balance;

}