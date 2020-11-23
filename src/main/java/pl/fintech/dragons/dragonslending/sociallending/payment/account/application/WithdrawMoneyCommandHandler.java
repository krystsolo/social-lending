package pl.fintech.dragons.dragonslending.sociallending.payment.account.application;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.BankApiService;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.MoneyTransferEvent.MoneyWithdrawn;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.SystemAccountNumber;
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
public class WithdrawMoneyCommandHandler {

    private final AccountRepository accountRepository;
    private final EventPublisher eventPublisher;
    private final AuthenticationFacade authenticationFacade;
    private final BankApiService bankApiService;

    public void withdraw(@NonNull UUID requestedAccountNumber, @NonNull BigDecimal amount) {
        UUID userId = authenticationFacade.idOfCurrentLoggedUser();
        Account userAccount = accountRepository.getOneByUserId(userId);
        userAccount.withdraw(amount);
        SystemAccountNumber systemAccountNumber = accountRepository.getSystemAccountNumber();
        bankApiService.requestWithdraw(
                systemAccountNumber.number(),
                requestedAccountNumber,
                amount);
        eventPublisher.publish(
                MoneyWithdrawn.now(
                        userAccount.getId(),
                        requestedAccountNumber,
                        amount));
    }
}
