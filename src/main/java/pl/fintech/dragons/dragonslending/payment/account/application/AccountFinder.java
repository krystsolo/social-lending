package pl.fintech.dragons.dragonslending.payment.account.application;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.payment.account.domain.SystemAccountNumber;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountFinder {

    private final AccountRepository accountRepository;

    public SystemAccountNumber getSystemAccount() {
        return accountRepository.getSystemAccountNumber();
    }
}
