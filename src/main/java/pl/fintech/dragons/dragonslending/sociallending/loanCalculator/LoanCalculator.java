package pl.fintech.dragons.dragonslending.sociallending.loanCalculator;

import java.math.BigDecimal;

public interface LoanCalculator {
  LoanCalculation calculate(BigDecimal loanAmount, Integer timePeriod, Float interestRate);
}
