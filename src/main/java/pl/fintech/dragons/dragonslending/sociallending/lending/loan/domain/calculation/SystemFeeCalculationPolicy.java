package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SystemFeeCalculationPolicy {

    private static final double FEE_RATE = 0.02;

    public BigDecimal percentageSystemFee(BigDecimal loanAmount) {
        return loanAmount
                .multiply(BigDecimal.valueOf(FEE_RATE));
    }

}
