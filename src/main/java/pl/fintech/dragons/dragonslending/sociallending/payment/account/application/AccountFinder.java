package pl.fintech.dragons.dragonslending.sociallending.payment.account.application;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountInfo;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.SystemAccountNumber;
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountFinder {

    private final AccountRepository accountRepository;
    private final AuthenticationFacade authenticationFacade;

    public SystemAccountNumber getSystemAccount() {
        return accountRepository.getSystemAccountNumber();
    }

    public AccountInfo getAccountInfoOfCurrentLoggedUser() {
        UUID userId = authenticationFacade.idOfCurrentLoggedUser();
        Account account = accountRepository.getOneByUserId(userId);
        return AccountInfo.from(account);
    }
}
