package fptu.fcharity.repository;

import fptu.fcharity.entity.TransactionHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID> {
    @EntityGraph(attributePaths = {"targetWallet"})
    List<TransactionHistory> findTransactionHistoryByWalletId(UUID id);
    // Define custom query methods if needed
}