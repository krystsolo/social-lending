package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain;

import lombok.Value;

import java.math.BigDecimal;

@Value
class Repayment {
    BigDecimal lenderAmount;
    BigDecimal systemAmount;

    BigDecimal totalAmount() {
        return lenderAmount.add(systemAmount);
    }
}
