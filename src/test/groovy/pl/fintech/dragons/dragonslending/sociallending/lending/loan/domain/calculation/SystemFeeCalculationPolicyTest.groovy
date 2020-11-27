package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation

import spock.lang.Specification

class SystemFeeCalculationPolicyTest extends Specification {

    SystemFeeCalculationPolicy systemFeeCalculationPolicy = new SystemFeeCalculationPolicy()

    def "should calculate percentage system fee from amount"() {
        given:
        BigDecimal amount = BigDecimal.TEN

        when:
        def systemFee = systemFeeCalculationPolicy.percentageSystemFee(amount)

        then:
        systemFee == amount * BigDecimal.valueOf(0.02)
    }
}
