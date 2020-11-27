package pl.fintech.dragons.dragonslending.sociallending.lending.loan.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.Loan;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanRepository;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class LoanRepositoryAdapter implements LoanRepository {

    private final LoanRepositoryAdapter.LoanJpaRepository repository;

    @Override
    public void save(Loan loan) {
        repository.save(loan);
    }

    @Override
    public Loan getOne(UUID loanId) {
        return repository.getOne(loanId);
    }

    @Override
    public List<Loan> getAllByUserId(UUID userId) {
        return repository.getAllByBorrowerIdOrLenderId(userId, userId);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public List<Loan> findAll() {
        return repository.findAll();
    }

    @Override
    public Set<Loan> findAllLoansWithUnpaidInstallments() {
        return repository.findAllLoansWithUnpaidInstallments();
    }

    @Override
    public List<Loan> findAllLoansWithTodayAsNextInstallmentDay() {
        return repository.findAllByStatusAndNextInstallmentDateEquals(LoanStatus.ACTIVE, LocalDate.now());
    }

    interface LoanJpaRepository extends JpaRepository<Loan, UUID> {

        @Query("SELECT DISTINCT loan FROM Loan loan JOIN loan.installments installment WHERE installment.status = 'UNPAID'")
        Set<Loan> findAllLoansWithUnpaidInstallments();

        List<Loan> findAllByStatusAndNextInstallmentDateEquals(LoanStatus status, LocalDate localDate);

        List<Loan> getAllByBorrowerIdOrLenderId(UUID borrowerId, UUID lenderId);
    }
}
