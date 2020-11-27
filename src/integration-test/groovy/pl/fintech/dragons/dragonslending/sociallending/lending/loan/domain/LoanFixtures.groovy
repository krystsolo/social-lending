package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain


import java.time.LocalDate
import java.time.LocalDateTime

class LoanFixtures {

    static LoanInstallment UNPAID_LOAN_INSTALLMENT = new LoanInstallment(
            UUID.randomUUID(), BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now(), LocalDate.now(), LoanInstallment.InstallmentStatus.UNPAID);
    static LoanInstallment PAID_LOAN_INSTALLMENT = new LoanInstallment(
            UUID.randomUUID(), BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now(), LocalDate.now(), LoanInstallment.InstallmentStatus.PAID);

    static Loan loanWithPaidInstallmentFor(UUID borrowerId, UUID lenderId) {
        return new Loan(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now(), 10, LoanStatus.ACTIVE, Arrays.asList(LoanDataFixtures.PAID_LOAN_INSTALLMENT))
    }

    static Loan loanWithUnpaidInstallmentFor(UUID borrowerId, UUID lenderId) {
        return new Loan(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now(), 10, LoanStatus.ACTIVE, Arrays.asList(LoanDataFixtures.UNPAID_LOAN_INSTALLMENT))
    }

    static Loan loanWithTodayAsNextInstallment(UUID borrowerId, UUID lenderId) {
        return new Loan(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now(), 10, LoanStatus.ACTIVE, Arrays.asList(LoanDataFixtures.PAID_LOAN_INSTALLMENT))
    }

    static Loan loanWithNotTodayAsInstallmentDay(UUID borrowerId, UUID lenderId) {
        return new Loan(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now().plusMonths(1), 10, LoanStatus.ACTIVE, Arrays.asList(LoanDataFixtures.PAID_LOAN_INSTALLMENT))
    }
}
