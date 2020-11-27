package pl.fintech.dragons.dragonslending.sociallending.payment.account.application


import pl.fintech.dragons.dragonslending.sociallending.offer.OfferSubmitted
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferTerminated
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository
import spock.lang.Specification

class OfferEventHandlerTest extends Specification {

    AccountRepository accountRepository = Mock(AccountRepository)
    OfferEventHandler offerEventHandler = new OfferEventHandler(accountRepository)

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
}
