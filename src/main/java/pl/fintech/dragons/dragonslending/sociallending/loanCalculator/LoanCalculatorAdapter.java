package pl.fintech.dragons.dragonslending.sociallending.loanCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LoanCalculatorAdapter implements LoanCalculator {

  private static final double FEE_RATE = 0.02;

  @Override
  public LoanCalculationDto calculate(BigDecimal loanAmount, Integer timePeriod, Float interestRate) {

    BigDecimal finalValue = loanAmount
        .add(loanAmount
            .multiply(BigDecimal.valueOf(timePeriod))
            .multiply(BigDecimal.valueOf((interestRate / 100) / 12)))
        .add(loanAmount
            .multiply(BigDecimal.valueOf(FEE_RATE)));

    return LoanCalculationDto.builder()
        .finalValue(finalValue.setScale(2, RoundingMode.HALF_UP))
        .periodValue(finalValue.divide(BigDecimal.valueOf(timePeriod), 2, RoundingMode.HALF_UP))
        .build();
  }
}
