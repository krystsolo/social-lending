package pl.fintech.dragons.dragonslending.payment.account.infrastructure.repository;

import org.springframework.stereotype.Repository;
import pl.fintech.dragons.dragonslending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.payment.account.domain.AccountRepository;

import java.util.UUID;

@Repository
public interface AccountRepositoryAdapter extends AccountRepository, org.springframework.data.repository.Repository<Account, UUID> {

    Account getOne(UUID accountId);

    void save(Account account);
}
