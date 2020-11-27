package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/loans/")
@RequiredArgsConstructor
@Slf4j
class LoanController {

    private final LoanFinder loanFinder;

    @GetMapping("/self")
    LoansView getCurrentLoggedUserLoans() {
        return loanFinder.getLoansForCurrentUser();
    }

    @GetMapping("/{id}")
    LoanDetailsView getLoanDetails(@PathVariable UUID id) {
        return loanFinder.getLoanDetailsFor(id);
    }
}
