package pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.fintech.dragons.dragonslending.PostgreSQLContainerSpecification
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.User
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRepository
import pl.fintech.dragons.dragonslending.sociallending.identity.infrastructure.UserConfig
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository
import spock.lang.Subject

@DataJpaTest
@ActiveProfiles("integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [AccountRepoConfig.class, UserConfig.class])
class AccountRepositoryAdapterIT extends PostgreSQLContainerSpecification {

    @Subject
    @Autowired
    private AccountRepository accountRepository

    @Autowired
    private UserRepository userRepository

    private static final String systemUsername = "ERSA_SYSTEM_ACCOUNT"

    def "should save account"() {
        given:
        UUID idOfUser = UUID.randomUUID()
        saveUser(idOfUser)
        Account account = new Account(UUID.randomUUID(), idOfUser, BigDecimal.ONE, BigDecimal.TEN)

        when:
        accountRepository.save(account)

        then:
        def fromDb = accountRepository.getAll()
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == account.id
            userId == account.userId
            balance == account.balance
            frozenAmount == account.frozenAmount
        }
    }

    def "should retrieve account"() {
        given:
        UUID idOfUser = UUID.randomUUID()
        saveUser(idOfUser)
        Account account = new Account(UUID.randomUUID(), idOfUser, BigDecimal.ONE, BigDecimal.TEN)
        accountRepository.save(account)

        when:
        def fromDb = accountRepository.getOne(account.id)

        then:
        with(fromDb) {
            id == account.id
            userId == account.userId
            balance == account.balance
            frozenAmount == account.frozenAmount
        }
    }

    def "should find system user id by system username"() {
        given:
        UUID idOfUser = UUID.randomUUID()
        final User user = new User(idOfUser, systemUsername + "@com.com",
                systemUsername, systemUsername, systemUsername, systemUsername)
        userRepository.save(user)
        Account account = new Account(UUID.randomUUID(), idOfUser, BigDecimal.ONE, BigDecimal.TEN)
        accountRepository.save(account)

        when:
        def fromDb = accountRepository.getSystemAccountNumber()

        then:
        with(fromDb) {
            number() == account.id
        }
    }


    def "should retrieve account be user id"() {
        given:
        UUID idOfUser = UUID.randomUUID()
        saveUser(idOfUser)
        Account account = new Account(UUID.randomUUID(), idOfUser, BigDecimal.ONE, BigDecimal.TEN)
        accountRepository.save(account)

        when:
        def fromDb = accountRepository.getOneByUserId(idOfUser)

        then:
        with(fromDb) {
            id == account.id
            userId == account.userId
            balance == account.balance
            frozenAmount == account.frozenAmount
        }
    }

    private void saveUser(UUID userId) {
        final User user = new User(userId, "email@com.com",
                "username", "firstname", "lastname", "password")
        userRepository.save(user)
    }
}
