package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
public class LoanInstallmentQuery {
    BigDecimal repaymentAmount;
    LocalDate timelyRepaymentTime;
    LocalDate repaymentTime;
    String status;
}
