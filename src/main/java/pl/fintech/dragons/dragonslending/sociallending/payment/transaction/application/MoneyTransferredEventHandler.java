package pl.fintech.dragons.dragonslending.sociallending.payment.transaction.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRegistered;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.MoneyTransferEvent;
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransaction;
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransactionRepository;

@RequiredArgsConstructor
@Transactional
class MoneyTransferredEventHandler {

    private final MoneyTransactionRepository moneyTransactionRepository;

    @EventListener
    public void handle(MoneyTransferEvent event) {
        MoneyTransaction moneyTransaction = new MoneyTransaction(
                event.getSourceAccountNumber(),
                event.getTargetAccountNumber(),
                event.type(),
                event.getAmount());
        moneyTransactionRepository.save(moneyTransaction);
    }
}
