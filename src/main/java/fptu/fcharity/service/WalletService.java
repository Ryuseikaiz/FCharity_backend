package fptu.fcharity.service;

import fptu.fcharity.entity.Wallet;
import fptu.fcharity.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;
    @Transactional
    public Wallet save() {
        Wallet wallet = new Wallet();
        wallet.setBalance(0);
        return walletRepository.save(wallet);
    }
}
