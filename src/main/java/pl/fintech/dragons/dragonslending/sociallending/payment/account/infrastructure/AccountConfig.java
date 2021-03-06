package pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.common.events.publisher.EventPublisherConfig;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.*;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.BankApiService;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.bankapi.BankApiConfig;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.initializer.SystemAccountConfig;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.repository.AccountRepoConfig;
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade;

@Configuration
@Import({BankApiConfig.class, SystemAccountConfig.class, AccountRepoConfig.class, EventPublisherConfig.class})
public class AccountConfig {

    @Bean
    AccountFinder accountFinder(AccountRepository accountRepository, AuthenticationFacade authenticationFacade) {
        return new AccountFinder(accountRepository,authenticationFacade);
    }

    @Bean
    UserRegisteredEventHandler userRegisteredEventHandler(AccountRepository accountRepository) {
        return new UserRegisteredEventHandler(accountRepository);
    }

    @Bean
    MoneyDepositedFromExternalSourceEventHandler moneyDepositedFromExternalSourceEventHandler(EventPublisher eventPublisher, AccountRepository accountRepository) {
        return new MoneyDepositedFromExternalSourceEventHandler(eventPublisher, accountRepository);
    }

    @Bean
    WithdrawMoneyCommandHandler withdrawMoneyCommandHandler(AccountRepository accountRepository, EventPublisher eventPublisher,
                                                            AuthenticationFacade authenticationFacade, BankApiService bankApiService) {
        return new WithdrawMoneyCommandHandler(accountRepository, eventPublisher, authenticationFacade, bankApiService);
    }

    @Bean
    OfferEventHandler offerEventHandler(AccountRepository accountRepository, EventPublisher eventPublisher) {
        return new OfferEventHandler(accountRepository, eventPublisher);
    }

    @Bean
    LoanRepaymentEventHandler loanRepaymentEventHandler(AccountRepository accountRepository, EventPublisher eventPublisher) {
        return new LoanRepaymentEventHandler(accountRepository, eventPublisher);
    }
}
