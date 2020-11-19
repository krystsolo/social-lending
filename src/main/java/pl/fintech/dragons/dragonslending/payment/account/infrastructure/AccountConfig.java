package pl.fintech.dragons.dragonslending.payment.account.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.fintech.dragons.dragonslending.payment.account.infrastructure.bankapi.BankApiConfig;
import pl.fintech.dragons.dragonslending.payment.account.infrastructure.initializer.SystemAccountConfig;
import pl.fintech.dragons.dragonslending.payment.account.infrastructure.repository.AccountRepoConfig;

@Configuration
@Import({BankApiConfig.class, SystemAccountConfig.class, AccountRepoConfig.class})
public class AccountConfig {
}
