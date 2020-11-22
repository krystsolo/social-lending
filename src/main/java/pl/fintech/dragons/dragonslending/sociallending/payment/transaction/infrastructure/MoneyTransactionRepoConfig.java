package pl.fintech.dragons.dragonslending.sociallending.payment.transaction.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransaction;
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransactionRepository;


@Configuration
@EnableJpaRepositories(basePackageClasses = MoneyTransactionRepositoryAdapter.class)
@EntityScan(basePackageClasses = MoneyTransaction.class)
public class MoneyTransactionRepoConfig {
}
