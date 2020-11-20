package pl.fintech.dragons.dragonslending.sociallending.payment.account.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.MoneyTransferEvent;
import pl.fintech.dragons.dragonslending.paymentplatformmock.MoneyDepositedFromExternalSource;

@RequiredArgsConstructor
@Transactional
public class MoneyDepositedFromExternalSourceEventHandler {

    private final EventPublisher eventPublisher;
    private final AccountRepository accountRepository;

    @EventListener
    public void handle(MoneyDepositedFromExternalSource event) {
        Account account = accountRepository.getOneByUserId(event.getUserId());
        account.deposit(event.getAmount());
        eventPublisher.publish(
                MoneyTransferEvent.MoneyDeposited.now(
                        event.getSourceAccountNumber(),
                        account.getId(),
                        event.getAmount()));
    }
}
