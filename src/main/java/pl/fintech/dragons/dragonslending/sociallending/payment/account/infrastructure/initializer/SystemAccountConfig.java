package pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRepository;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.bankapi.BankApi;

@Configuration
@Slf4j
public class SystemAccountConfig {

    private final String systemUsername;

    public SystemAccountConfig(@Value("${system.user.name}") String systemUsername) {
        this.systemUsername = systemUsername;
    }

    @Bean
    IdentityLocationHeaderRetriever identityLocationHeaderRetriever() {
        return new IdentityLocationHeaderRetriever();
    }

    @Profile({"sit | uat | prod"})
    @Bean
    CommandLineRunner init(UserRepository userRepository,
                           BankApi bankApi,
                           AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder,
                           IdentityLocationHeaderRetriever identityLocationHeaderRetriever) {
        return args -> {
            new BankApiIntegrator(userRepository, bankApi, accountRepository,
                    passwordEncoder, identityLocationHeaderRetriever, systemUsername)
                    .integrate();
        };
    }
}