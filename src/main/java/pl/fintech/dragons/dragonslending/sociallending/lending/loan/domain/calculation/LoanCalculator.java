package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class LoanCalculator {

  private final SystemFeeCalculationPolicy systemFeeCalculationPolicy;

  public LoanCalculation calculate(BigDecimal loanAmount, Integer timePeriod, Float interestRate) {

    BigDecimal systemFee = systemFeeCalculationPolicy.percentageSystemFee(loanAmount);

    BigDecimal amountToRepaidToLender = loanAmount
        .add(loanAmount
            .multiply(BigDecimal.valueOf(timePeriod))
            .multiply(BigDecimal.valueOf((interestRate / 100) / 12)))
            .setScale(2, RoundingMode.HALF_UP);

    return new LoanCalculation(amountToRepaidToLender, systemFee);
  }
}
