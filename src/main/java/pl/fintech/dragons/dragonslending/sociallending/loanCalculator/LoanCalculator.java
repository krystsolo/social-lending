package pl.fintech.dragons.dragonslending.sociallending.loanCalculator;

import java.math.BigDecimal;

public interface LoanCalculator {
  LoanCalculationDto calculate(BigDecimal loanAmount, Integer timePeriod, Float interestRate);
}
