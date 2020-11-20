package pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationConfig;


@Configuration
@EnableJpaRepositories(basePackageClasses = AccountRepositoryAdapter.class, considerNestedRepositories = true)
@EntityScan(basePackageClasses = Account.class)
@Import(AuthenticationConfig.class)
public class AccountRepoConfig {

    private final String systemUsername;

    public AccountRepoConfig(@Value("${system.user.name}") String systemUsername) {
        this.systemUsername = systemUsername;
    }

    @Bean
    AccountRepository accountRepository(AccountRepositoryAdapter.AccountJpaRepository jpaRepo) {
        return new AccountRepositoryAdapter(jpaRepo, systemUsername);
    }
}
