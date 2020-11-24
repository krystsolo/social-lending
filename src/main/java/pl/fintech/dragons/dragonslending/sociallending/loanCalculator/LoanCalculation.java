package pl.fintech.dragons.dragonslending.sociallending.loanCalculator;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Builder
@Value
public class LoanCalculation {
  BigDecimal finalValue;
  BigDecimal periodValue;
}
