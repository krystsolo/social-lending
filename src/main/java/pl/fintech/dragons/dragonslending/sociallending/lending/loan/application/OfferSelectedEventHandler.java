package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculator;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanRepository;

@RequiredArgsConstructor
@Transactional
public class OfferSelectedEventHandler {

    private final LoanCalculator loanCalculator;
    private final LoanRepository loanRepository;

    @EventListener
    public void handle() {

    }
}
