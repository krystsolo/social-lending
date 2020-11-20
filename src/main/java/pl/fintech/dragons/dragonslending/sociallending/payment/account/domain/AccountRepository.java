package pl.fintech.dragons.dragonslending.sociallending.payment.account.domain;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    void save(Account account);

    Account getOne(UUID accountId);

    Account getOneByUserId(UUID userId);

    List<Account> getAll();

    SystemAccountNumber getSystemAccountNumber();
}
