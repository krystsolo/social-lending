package pl.fintech.dragons.dragonslending.payment.account.infrastructure.initializer

import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import pl.fintech.dragons.dragonslending.identity.domain.User
import pl.fintech.dragons.dragonslending.identity.domain.UserRepository
import pl.fintech.dragons.dragonslending.payment.account.domain.Account
import pl.fintech.dragons.dragonslending.payment.account.domain.AccountRepository
import pl.fintech.dragons.dragonslending.payment.account.infrastructure.bankapi.BankApi
import spock.lang.Specification

class BankApiIntegratorTest extends Specification {
    String username = "ERSA_SYSTEM_ACCOUNT"
    IdentityLocationHeaderRetriever identityLocationHeaderRetriever = Mock(IdentityLocationHeaderRetriever)
    UserRepository userRepository = Mock(UserRepository)
    BankApi bankApi = Mock(BankApi)
    AccountRepository accountRepository = Mock(AccountRepository)
    PasswordEncoder passwordEncoder = Mock(PasswordEncoder)

    BankApiIntegrator bankApiIntegrator = new BankApiIntegrator(
            userRepository, bankApi, accountRepository,
            passwordEncoder, identityLocationHeaderRetriever, username)

    def "should use already existing in db system account data"() {
        given:
        UUID userId = UUID.randomUUID()
        User user = new User(userId, username + "@sociallending.com",
                username, username, username, username)
        userRepository.findByUsername(username) >> Optional.of(user)

        UUID accountId = UUID.randomUUID()
        Account account = new Account(accountId, userId, BigDecimal.ZERO, BigDecimal.ZERO)
        accountRepository.getOneByUserId(userId) >> account

        when:
        bankApiIntegrator.integrate()

        then:
        0 * bankApi.createAccount(_)
        0 * userRepository.save(_)
        0 * accountRepository.save(_)
    }

    def "should create system user and account on application start when there is no such account and user existing in db"() {
        given:
        UUID userId = UUID.randomUUID()
        User user = new User(userId, username + "@sociallending.com",
                username, username, username, username)
        userRepository.findByUsername(username) >> Optional.of(user)

        accountRepository.getOneByUserId(userId) >> null
        UUID accountId = UUID.randomUUID()
        identityLocationHeaderRetriever.retrieve(_ as ResponseEntity) >> accountId

        when:
        bankApiIntegrator.integrate()

        then:
        1 * bankApi.createAccount(new BankApi.UserId(userId.toString())) >> ResponseEntity.status(201).build()
        0 * userRepository.save(_)
        1 * accountRepository.save(_)
    }

    def "should create system account on application start when there is no such account existing in db but user exists"() {
        given:
        userRepository.findByUsername(username) >> Optional.empty()
        UUID accountId = UUID.randomUUID()
        identityLocationHeaderRetriever.retrieve(_ as ResponseEntity) >> accountId

        when:
        bankApiIntegrator.integrate()

        then:
        1 * bankApi.createAccount(_) >> ResponseEntity.status(201).build()
        1 * userRepository.save(_)
        1 * accountRepository.save(_)
    }
}
