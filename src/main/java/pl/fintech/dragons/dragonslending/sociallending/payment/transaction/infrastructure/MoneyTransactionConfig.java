package pl.fintech.dragons.dragonslending.sociallending.payment.transaction.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.application.MoneyTransferredEventHandler;
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransaction;


@Configuration
@EnableJpaRepositories(basePackageClasses = MoneyTransactionRepositoryAdapter.class)
@EntityScan(basePackageClasses = MoneyTransaction.class)
public class MoneyTransactionConfig {

    @Bean
    MoneyTransferredEventHandler moneyTransferredEventHandler(MoneyTransactionRepositoryAdapter moneyTransactionRepositoryAdapter) {
        return new MoneyTransferredEventHandler(moneyTransactionRepositoryAdapter);
    }
}
