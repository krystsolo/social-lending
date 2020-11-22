package pl.fintech.dragons.dragonslending.sociallending.payment.transaction.application

import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.MoneyTransferEvent
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransaction
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransactionRepository
import spock.lang.Specification

class MoneyTransferredEventHandlerTest extends Specification {

    MoneyTransactionRepository repository = Mock(MoneyTransactionRepository)
    MoneyTransferredEventHandler handler = new MoneyTransferredEventHandler(repository)

    def "should create new money transaction when money transfer event occurs"() {
        given:
        MoneyTransferEvent event = MoneyTransferEvent.MoneyDeposited
                .now(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN)

        when:
        handler.handle(event)

        then:
        1 * repository.save(
                {
                    it.sourceAccountNumber == event.sourceAccountNumber
                    it.targetAccountNumber == event.targetAccountNumber
                    it.type == event.type()
                    it.amount == event.amount
                } as MoneyTransaction
        )
    }
}
