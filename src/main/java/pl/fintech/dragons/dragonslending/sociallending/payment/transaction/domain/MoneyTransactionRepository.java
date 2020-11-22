package pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain;

import java.util.List;

public interface MoneyTransactionRepository {

    void save(MoneyTransaction moneyTransaction);

    List<MoneyTransaction> findAll();
}
