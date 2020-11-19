package pl.fintech.dragons.dragonslending.payment.account.infrastructure.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.fintech.dragons.dragonslending.identity.domain.User;
import pl.fintech.dragons.dragonslending.identity.domain.UserRepository;
import pl.fintech.dragons.dragonslending.payment.account.domain.Account;
import pl.fintech.dragons.dragonslending.payment.account.domain.AccountRepository;
import pl.fintech.dragons.dragonslending.payment.account.infrastructure.bankapi.BankApi;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class BankApiIntegrator {

    private final UserRepository userRepository;
    private final BankApi bankApi;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdentityLocationHeaderRetriever identityLocationHeaderRetriever;

    private final String systemUsername;

    void integrate() {
        log.debug("System username: {}", systemUsername);
        Optional<User> userByUsername = userRepository.findByUsername(systemUsername);
        UUID systemUserId;
        UUID systemAccountNumber;
        if (userByUsername.isPresent()) {
            systemUserId = userByUsername.get().getId();
            Account systemAccount = accountRepository.getOneByUserId(systemUserId);
            if (systemAccount == null) {
                systemAccountNumber = createSystemAccount(systemUserId, bankApi, accountRepository);
            } else {
                systemAccountNumber = systemAccount.getId();
            }

        } else {
            systemUserId = createSystemUser(userRepository, passwordEncoder);
            systemAccountNumber = createSystemAccount(systemUserId, bankApi, accountRepository);
        }

        log.info("============================\n" +
                "Running system with: \n" +
                "System username: " + systemUsername + "\n" +
                "System account number: " + systemAccountNumber + "\n" +
                "System user id: " + systemUserId + "\n"
        );
    }

    private UUID createSystemUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        UUID systemUserId = UUID.randomUUID();
        User user = new User(systemUserId,
                systemUsername + "@sociallending.com",
                systemUsername,
                systemUsername,
                systemUsername,
                passwordEncoder.encode(systemUsername));
        userRepository.save(user);
        log.info("Created system user: {}", user);
        return systemUserId;
    }

    private UUID createSystemAccount(UUID systemUserId, BankApi bankApi, AccountRepository accountRepository) {
        UUID systemAccountNumber = createAccountInBankApi(systemUserId, bankApi);
        accountRepository.save(
                new Account(systemAccountNumber, systemUserId, BigDecimal.ZERO, BigDecimal.ZERO));
        log.info("Created system account: {}", systemAccountNumber);
        return systemAccountNumber;
    }

    private UUID createAccountInBankApi(UUID systemUserId, BankApi bankApi) {
        ResponseEntity<Void> account = bankApi.createAccount(new BankApi.UserId(systemUserId.toString()));
        return identityLocationHeaderRetriever.retrieve(account);
    }
}
