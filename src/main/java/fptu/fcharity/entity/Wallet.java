package fptu.fcharity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @Column(name = "wallet_id", nullable = false)
    private UUID id;

    @Nationalized
    @Column(name = "balance")
    private String balance;

}