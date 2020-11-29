package pl.fintech.dragons.dragonslending.sociallending.payment.account.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferSelected;
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferSubmitted;
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferTerminated;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.MoneyTransferEvent;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Transactional
public class OfferEventHandler {

    private final AccountRepository accountRepository;
    private final EventPublisher eventPublisher;

    @EventListener
    public void handleOfferSubmitted(OfferSubmitted event) {
        Account account = accountRepository.getOneByUserId(event.getUserId());
        account.freeze(event.getAmount());
    }

    @EventListener
    public void handleOfferTerminated(OfferTerminated event) {
        Account account = accountRepository.getOneByUserId(event.getUserId());
        account.unfreeze(event.getAmount());
    }

    @EventListener
    public void handleOfferSelection(OfferSelected event) {
        BigDecimal amount = event.getAmount();

        Account lenderAccount = accountRepository.getOneByUserId(event.getLenderId());
        lenderAccount.unfreeze(amount);
        lenderAccount.withdraw(amount);

        Account borrowerAccount = accountRepository.getOneByUserId(event.getBorrowerId());
        borrowerAccount.deposit(amount);

        eventPublisher.publish(
                MoneyTransferEvent.LendingMoneyTransferred.now(
                        event.getBorrowerId(),
                        event.getLenderId(),
                        event.getAmount()));
    }
}