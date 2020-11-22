package pl.fintech.dragons.dragonslending.sociallending.payment.transaction.infrastructure;

import org.springframework.stereotype.Repository;
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransaction;
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransactionRepository;

import java.util.UUID;

@Repository
interface MoneyTransactionRepositoryAdapter extends MoneyTransactionRepository,
        org.springframework.data.repository.Repository<MoneyTransaction, UUID> {

}
