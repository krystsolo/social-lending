package pl.fintech.dragons.dragonslending.payment.account.infrastructure.repository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.identity.domain.User;
import pl.fintech.dragons.dragonslending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.security.AuthenticationConfig;


@Configuration
@EnableJpaRepositories(basePackageClasses = AccountRepositoryAdapter.class, considerNestedRepositories = true)
@EntityScan(basePackageClasses = Account.class)
public class AccountRepoConfig {
}
