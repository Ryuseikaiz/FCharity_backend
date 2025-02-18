package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
public class Wallet {
    @Id
    @Column(name = "wallet_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID walletId;

    @Column(name = "balance", nullable = false)
    private String balance;
}