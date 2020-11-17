package pl.fintech.dragons.dragonslending.payment.account.domain;

import java.util.UUID;

public interface AccountRepository {

    void save(Account account);

    Account getOne(UUID userId);
}
