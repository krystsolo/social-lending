package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application

import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculation
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculator
import spock.lang.Specification

class LoanCalculationServiceTest extends Specification {

    LoanCalculator loanCalculator = Mock(LoanCalculator)
    LoanCalculationService loanCalculationService = new LoanCalculationService(loanCalculator)

    def "should calculate and return loan calculation"() {
        given:
        BigDecimal loanAmount = BigDecimal.TEN
        int timePeriod = 10
        float interestRate = 2.0
        def calculation = new LoanCalculation(loanAmount, BigDecimal.ONE)

        when:
        def repaid = loanCalculationService.calculateAmountsToRepaid(loanAmount, timePeriod, interestRate)

        then:
        1 * loanCalculator.calculate(loanAmount, timePeriod, interestRate) >> calculation
        and:
        repaid == calculation
    }
}
