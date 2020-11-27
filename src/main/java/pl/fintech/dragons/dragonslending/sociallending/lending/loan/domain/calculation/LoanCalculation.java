package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class LoanCalculation {
  BigDecimal amountToRepaidToLender;
  BigDecimal systemFee;

  public BigDecimal totalAmountToRepaid() {
    return amountToRepaidToLender.add(systemFee);
  }
}
