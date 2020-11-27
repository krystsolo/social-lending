package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.Loan;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanFinder {

    private final LoanRepository loanRepository;
    private final UserService userService;
    private final LoanViewAssembler loanViewAssembler;

    public LoansView getLoansForCurrentUser() {
        UUID userId = userService.getCurrentLoggedUser().getId();
        List<Loan> loans = loanRepository.getAllByUserId(userId);
        return loanViewAssembler.loanViewFrom(loans, userId);
    }

    public LoanDetailsView getLoanDetailsFor(UUID loanId) {
        Loan loan = loanRepository.getOne(loanId);
        UUID userId = userService.getCurrentLoggedUser().getId();
        checkIfUserHasPermissionsToViewLoanDetails(loan, userId);
        return loanViewAssembler.loanDetailsViewFrom(loan, userId);
    }

    private void checkIfUserHasPermissionsToViewLoanDetails(Loan loan, UUID userId) {
        if (!loan.getBorrowerId().equals(userId) && !loan.getLenderId().equals(userId)) {
            throw new AccessDeniedException("Only borrower or lender can access details of loan");
        }
    }
}
