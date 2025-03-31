package fptu.fcharity.service;

import fptu.fcharity.entity.Wallet;
import fptu.fcharity.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;
    public Wallet save() {
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setBalance(0);
        return walletRepository.save(wallet);
    }
}
