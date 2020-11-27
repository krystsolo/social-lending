package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface LoanRepository {

    void save(Loan loan);

    List<Loan> findAll();

    Set<Loan> findAllLoansWithUnpaidInstallments();

    List<Loan> findAllLoansWithTodayAsNextInstallmentDay();

    Loan getOne(UUID loanId);

    List<Loan> getAllByUserId(UUID userId);

    void deleteAll();
}
