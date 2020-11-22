package pl.fintech.dragons.dragonslending.sociallending.payment.transaction.infrastructure

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.fintech.dragons.dragonslending.PostgreSQLContainerSpecification
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransaction
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransactionRepository
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransactionType
import spock.lang.Subject

@DataJpaTest
@ActiveProfiles("integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [MoneyTransactionRepoConfig.class])
class MoneyTransactionRepositoryAdapterTest extends PostgreSQLContainerSpecification {

    @Subject
    @Autowired
    private MoneyTransactionRepository moneyTransactionRepository

    def "should save new money transaction"() {
        given:
        MoneyTransaction moneyTransaction = new MoneyTransaction(
                UUID.randomUUID(), UUID.randomUUID(), MoneyTransactionType.DEPOSIT, BigDecimal.TEN)

        when:
        moneyTransactionRepository.save(moneyTransaction)

        then:
        def fromDb = moneyTransactionRepository.findAll()
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == moneyTransaction.id
            sourceAccountNumber == moneyTransaction.sourceAccountNumber
            targetAccountNumber == moneyTransaction.targetAccountNumber
            type == moneyTransaction.type
            amount == moneyTransaction.amount
        }
    }
}
