package pl.fintech.dragons.dragonslending.payment.account.infrastructure.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.fintech.dragons.dragonslending.identity.domain.User;
import pl.fintech.dragons.dragonslending.identity.domain.UserRepository;
import pl.fintech.dragons.dragonslending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.payment.account.infrastructure.bankapi.BankApi;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Configuration
@Slf4j
public class SystemAccountConfig {

    private final String systemUsername;
    private final IdentityLocationHeaderRetriever identityLocationHeaderRetriever;

    public SystemAccountConfig(@Value("${system.user.name}") String systemUsername,
                               IdentityLocationHeaderRetriever identityLocationHeaderRetriever) {
        this.systemUsername = systemUsername;
        this.identityLocationHeaderRetriever = identityLocationHeaderRetriever;
    }


    @Profile({"sit | uat | prod"})
    @Bean
    CommandLineRunner init(UserRepository userRepository,
                           BankApi bankApi,
                           AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder) {
        return args -> {
            new BankApiIntegrator(userRepository, bankApi, accountRepository,
                    passwordEncoder, identityLocationHeaderRetriever, systemUsername)
                    .integrate();
        };
    }
}