package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain


import java.time.LocalDate
import java.time.LocalDateTime

class LoanDataFixtures {

    static LoanInstallment UNPAID_LOAN_INSTALLMENT = new LoanInstallment(
            UUID.randomUUID(), BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO, LocalDate.now(), LocalDate.now(), LoanInstallment.InstallmentStatus.UNPAID)
    static LoanInstallment PAID_LOAN_INSTALLMENT = new LoanInstallment(
            UUID.randomUUID(), BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, LocalDate.now(), LocalDate.now(), LoanInstallment.InstallmentStatus.PAID)

    static Loan loanWithPaidInstallmentFor(UUID borrowerId, UUID lenderId) {
        return new Loan(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now(), 10, LoanStatus.ACTIVE, new ArrayList<LoanInstallment>(Arrays.asList(LoanDataFixtures.PAID_LOAN_INSTALLMENT)))
    }

    static Loan loanWithUnpaidInstallmentFor(UUID borrowerId, UUID lenderId) {
        return new Loan(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now(), 10, LoanStatus.ACTIVE, new ArrayList<LoanInstallment>(Arrays.asList(LoanDataFixtures.UNPAID_LOAN_INSTALLMENT)))
    }

    static Loan loanWithTodayAsNextInstallment(UUID borrowerId, UUID lenderId) {
        return new Loan(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now(), 10, LoanStatus.ACTIVE, new ArrayList<LoanInstallment>(Arrays.asList(LoanDataFixtures.PAID_LOAN_INSTALLMENT)))
    }

    static Loan loanWithNotTodayAsInstallmentDay(UUID borrowerId, UUID lenderId) {
        return new Loan(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now().plusMonths(1), 10, LoanStatus.ACTIVE, new ArrayList<LoanInstallment>(Arrays.asList(LoanDataFixtures.PAID_LOAN_INSTALLMENT)))
    }

    static Loan finishedLoanFor(UUID borrowerId, UUID lenderId) {
        return new Loan(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now().plusMonths(1), 1, LoanStatus.FINISHED, new ArrayList<LoanInstallment>(Arrays.asList(LoanDataFixtures.PAID_LOAN_INSTALLMENT)))
    }
}
