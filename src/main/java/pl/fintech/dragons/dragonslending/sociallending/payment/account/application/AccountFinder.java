package pl.fintech.dragons.dragonslending.sociallending.payment.account.application;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.SystemAccountNumber;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountFinder {

    private final AccountRepository accountRepository;

    public SystemAccountNumber getSystemAccount() {
        return accountRepository.getSystemAccountNumber();
    }
}
