package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(generator = "UUID")
    @ColumnDefault("newid()")
    @Column(name = "wallet_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID id;

    @Nationalized
    @Column(name = "balance")
    private String balance;

}