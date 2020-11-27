package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation


import spock.lang.Specification

class LoanCalculatorTest extends Specification {

    SystemFeeCalculationPolicy systemFeeCalculationPolicy = Mock(SystemFeeCalculationPolicy)
    LoanCalculator loanCalculator = new LoanCalculator(systemFeeCalculationPolicy)

    def "should return proper result of calculation"() {
        given:
        systemFeeCalculationPolicy.percentageSystemFee(_ as BigDecimal) >> BigDecimal.TEN

        when:
        LoanCalculation calculation = loanCalculator.calculate(BigDecimal.valueOf(1000), 12, 2)

        then:
        calculation.amountToRepaidToLender == 1020 as BigDecimal
        calculation.systemFee == BigDecimal.TEN
    }
}
