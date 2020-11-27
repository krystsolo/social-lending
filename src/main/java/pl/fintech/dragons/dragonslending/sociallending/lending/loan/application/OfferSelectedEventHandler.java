package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.Loan;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculation;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculator;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanRepository;
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferSelected;

@RequiredArgsConstructor
@Transactional
public class OfferSelectedEventHandler {

    private final LoanRepository loanRepository;
    private final LoanCalculator loanCalculator;

    @EventListener
    public void handle(OfferSelected event) {

        LoanCalculation loanCalculation = loanCalculator.calculate(
                event.getAmount(),
                event.getTimePeriod(),
                event.getInterestRate());

        Loan loan = Loan.from(
                event.getBorrowerId(),
                event.getLenderId(),
                loanCalculation,
                event.getTimePeriod());

        loanRepository.save(loan);
    }
}
