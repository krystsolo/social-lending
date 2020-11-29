package pl.fintech.dragons.dragonslending.sociallending.payment.account.application

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferSelected
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferSubmitted
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferTerminated
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.MoneyTransferEvent
import spock.lang.Specification

class OfferEventHandlerTest extends Specification {

    AccountRepository accountRepository = Mock(AccountRepository)
    EventPublisher eventPublisher = Mock(EventPublisher)
    OfferEventHandler offerEventHandler = new OfferEventHandler(accountRepository, eventPublisher)

    def "should handle offer submitted event"() {
        given:
        UUID userId = UUID.randomUUID()
        Account account = new Account(userId)
        account.deposit(100 as BigDecimal)

        def event = OfferSubmitted
                .now(UUID.randomUUID(), UUID.randomUUID(), userId, BigDecimal.TEN)

        when:
        offerEventHandler.handleOfferSubmitted(event)

        then:
        1 * accountRepository.getOneByUserId(userId) >> account
    }

    def "should handle offer terminated event"() {
        given:
        UUID userId = UUID.randomUUID()
        Account account = new Account(userId)
        account.deposit(100 as BigDecimal)
        account.freeze(100 as BigDecimal)

        def event = OfferTerminated
                .now(UUID.randomUUID(), UUID.randomUUID(), userId, BigDecimal.TEN)

        when:
        offerEventHandler.handleOfferTerminated(event)

        then:
        1 * accountRepository.getOneByUserId(userId) >> account
    }

    def "should handle offer selection event"() {
        given:
        UUID borrowerId = UUID.randomUUID()
        Account borrowerAccount = new Account(borrowerId)
        accountRepository.getOneByUserId(borrowerId) >> borrowerAccount

        UUID lenderId = UUID.randomUUID()
        Account lenderAccount = new Account(lenderId)
        lenderAccount.deposit(100 as BigDecimal)
        lenderAccount.freeze(BigDecimal.TEN)
        accountRepository.getOneByUserId(lenderId) >> lenderAccount

        when:
        offerEventHandler.handleOfferSelection(OfferSelected.now(
                UUID.randomUUID(), UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN, 2.0, 5))

        then:
        borrowerAccount.getBalance() == BigDecimal.TEN
        lenderAccount.getBalance() == 90 as BigDecimal
        1 * eventPublisher.publish(_ as MoneyTransferEvent.LendingMoneyTransferred)
    }
}
