package pl.fintech.dragons.dragonslending.payment.account.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.fintech.dragons.dragonslending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.payment.account.domain.SystemAccountNumber;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository repository;
    private final String systemUsername;

    @Override
    public void save(Account account) {
        repository.save(account);
    }

    @Override
    public Account getOne(UUID accountId) {
        return repository.getOne(accountId);
    }

    @Override
    public Account getOneByUserId(UUID userId) {
        return repository.getOneByUserId(userId);
    }

    @Override
    public List<Account> getAll() {
        return repository.findAll();
    }

    @Override
    public SystemAccountNumber getSystemAccountNumber() {
        UUID accountNumber = repository.getSystemAccountNumber(systemUsername);
        return SystemAccountNumber.of(accountNumber);
    }

    interface AccountJpaRepository extends JpaRepository<Account, UUID> {

        Account getOneByUserId(UUID accountId);

        @Query("SELECT a.id FROM Account a JOIN User u ON a.userId = u.id WHERE u.username = :username")
        UUID getSystemAccountNumber(String username);
    }
}
