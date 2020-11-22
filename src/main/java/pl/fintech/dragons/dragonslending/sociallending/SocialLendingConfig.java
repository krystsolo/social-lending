package pl.fintech.dragons.dragonslending.sociallending;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.fintech.dragons.dragonslending.common.events.publisher.EventPublisherConfig;
import pl.fintech.dragons.dragonslending.sociallending.identity.infrastructure.UserConfig;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.AccountConfig;
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.infrastructure.MoneyTransactionRepoConfig;
import pl.fintech.dragons.dragonslending.sociallending.security.SecurityConfig;

@Configuration
@Import({SecurityConfig.class, AccountConfig.class, UserConfig.class, EventPublisherConfig.class, MoneyTransactionRepoConfig.class})
public class SocialLendingConfig {
}
