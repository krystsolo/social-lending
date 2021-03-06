package pl.fintech.dragons.dragonslending.sociallending.payment.account.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRegistered;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository;

@RequiredArgsConstructor
@Transactional
public class UserRegisteredEventHandler {

    private final AccountRepository accountRepository;

    @EventListener
    public void handle(UserRegistered event) {
        Account account = new Account(event.getUserId());
        accountRepository.save(account);
    }
}
