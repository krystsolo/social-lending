package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculation;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculator;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Transactional
public class LoanCalculationService {

    private final LoanCalculator loanCalculator;

    public LoanCalculation calculateAmountsToRepaid(@NonNull BigDecimal loanAmount, int timePeriod, float interestRate) {
        return loanCalculator.calculate(loanAmount, timePeriod, interestRate);
    }
}
