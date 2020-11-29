package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application

import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanRepository
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculation
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculator
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferSelected
import spock.lang.Specification

class OfferSelectedEventHandlerTest extends Specification {

    LoanRepository loanRepository = Mock(LoanRepository)
    LoanCalculator loanCalculator = Mock(LoanCalculator)
    OfferSelectedEventHandler offerSelectedEventHandler = new OfferSelectedEventHandler(loanRepository, loanCalculator)

    def "should create new loan when offer selected event received"() {
        given:
        loanCalculator.calculate(BigDecimal.TEN, 5, 2.0) >> new LoanCalculation(BigDecimal.TEN, BigDecimal.TEN)

        when:
        offerSelectedEventHandler.handle(OfferSelected.now(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, 2.0, 5))

        then:
        1 * loanRepository.save(_)
    }
}
