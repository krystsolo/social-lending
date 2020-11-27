package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query;

import lombok.RequiredArgsConstructor;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.Loan;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanInstallmentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LoanViewAssembler {

    private final UserService userService;

    public LoansView loanViewFrom(List<Loan> loans, UUID userId) {

        List<LoanQuery> loansGranted = loans.stream()
                .filter(loan -> loan.getLenderId().equals(userId))
                .map(this::toGrantedLoanQuery)
                .collect(Collectors.toList());

        List<LoanQuery> loansTaken = loans.stream()
                .filter(loan -> loan.getBorrowerId().equals(userId))
                .map(this::toTakenLoanQuery)
                .collect(Collectors.toList());

        return new LoansView(loansGranted, loansTaken);
    }

    private LoanQuery toGrantedLoanQuery(Loan loan) {
        return new LoanQuery(
                loan.getId(),
                userService.getUser(loan.getBorrowerId()).getUsername(),
                loan.getAmountToRepaidToLender(),
                loan.getCreationTime(),
                loan.getNextInstallmentDate(),
                loan.getInstallmentsNumber());
    }

    private LoanQuery toTakenLoanQuery(Loan loan) {
        return new LoanQuery(
                loan.getId(),
                userService.getUser(loan.getLenderId()).getUsername(),
                loan.getAmountToRepaidToLender().add(loan.getSystemFee()),
                loan.getCreationTime(),
                loan.getNextInstallmentDate(),
                loan.getInstallmentsNumber());
    }

    public LoanDetailsView loanDetailsViewFrom(Loan loan, UUID userId) {
        List<LoanInstallmentQuery> installments = loan.getInstallments();
        if (loan.isActive()) {
            List<LoanInstallmentQuery> remainingInstallments = remainingInstallments(loan, installments.size());
            installments.addAll(remainingInstallments);
        }

        return new LoanDetailsView(
                loan.getId(),
                userId.equals(loan.getBorrowerId()) ?
                        userService.getUser(loan.getLenderId()).getUsername() : userService.getUser(loan.getBorrowerId()).getUsername(),
                userId.equals(loan.getBorrowerId()) ? LoanDetailsView.LoanType.TAKEN : LoanDetailsView.LoanType.GRANTED,
                loan.getCreationTime(),
                loan.getNextInstallmentDate(),
                loan.getAmountToRepaidToLender().add(loan.getSystemFee()),
                installments);
    }

    private List<LoanInstallmentQuery> remainingInstallments(Loan loan, int numberOfCurrentInstallments) {
        List<LoanInstallmentQuery> installments = new ArrayList<>();
        int remainingInstallments = loan.getInstallmentsNumber() - numberOfCurrentInstallments;
        for (int i = 0; i < remainingInstallments; i++) {
            installments.add(new LoanInstallmentQuery(
                    loan.calculatedInstallmentAmount(),
                    loan.getNextInstallmentDate().plusMonths(i),
                    null,
                    "UNPAID"));
        }
        return installments;
    }
}
