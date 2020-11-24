package pl.fintech.dragons.dragonslending.sociallending.loanCalculator


import spock.lang.Specification

class LoanCalculatorAdapterTest extends Specification {

    def "should return proper result of calculation"() {
        given:
        LoanCalculatorAdapter loanCalculator = new LoanCalculatorAdapter();

        when:
        LoanCalculationDto calculationDto = loanCalculator.calculate(BigDecimal.valueOf(1000), 2, 2.5)

        then:
        calculationDto.finalValue == LoanCalculatorFixtureData.CALCULATION_DTO.finalValue
        calculationDto.periodValue == LoanCalculatorFixtureData.CALCULATION_DTO.periodValue
    }
}
