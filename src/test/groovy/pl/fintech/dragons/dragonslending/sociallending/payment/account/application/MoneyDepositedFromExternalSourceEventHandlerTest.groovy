package pl.fintech.dragons.dragonslending.sociallending.payment.account.application

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.paymentplatformmock.MoneyDepositedFromExternalSource
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.MoneyTransferEvent
import spock.lang.Specification

class MoneyDepositedFromExternalSourceEventHandlerTest extends Specification {

    private EventPublisher eventPublisher = Mock(EventPublisher)
    private AccountRepository accountRepository = Mock(AccountRepository)
    def handler = new MoneyDepositedFromExternalSourceEventHandler(eventPublisher, accountRepository)

    def "should process MoneyDepositedFromExternalSource event"() {
        given:
        UUID userId = UUID.randomUUID()
        Account account = new Account(userId)
        accountRepository.getOneByUserId(userId) >> account

        def event = MoneyDepositedFromExternalSource
                .now(userId, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN)

        when:
        handler.handle(event)

        then:
        1 * eventPublisher.publish(
                {
                    it.sourceAccountNumber == event.getSourceAccountNumber()
                    it.targetAccountNumber == account.getId()
                    it.amount == event.getAmount()
                } as MoneyTransferEvent.MoneyDeposited)
    }
}
