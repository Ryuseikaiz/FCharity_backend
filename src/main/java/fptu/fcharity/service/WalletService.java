package fptu.fcharity.service;

import fptu.fcharity.entity.Wallet;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;
    @Transactional
    public Wallet save() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(0.00));
        return walletRepository.save(wallet);
    }
    @Transactional
    public Wallet getById(UUID id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Wallet not found"));
        return wallet;
    }
}
