package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain

import pl.fintech.dragons.dragonslending.common.events.DomainEvent
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountInfo
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class LoanTest extends Specification {

    def "should not paid off next installment when borrower balance is empty"() {
        given:
        Loan loan = LoanDataFixtures.loanWithTodayAsNextInstallment(UUID.randomUUID(), UUID.randomUUID())
        def installmentsNumberBefore = loan.getInstallments().size()
        AccountInfo accountInfo = new AccountInfo(BigDecimal.ZERO, BigDecimal.ZERO)

        when:
        def domainEvents = loan.payOffNextInstallment(accountInfo)

        then:
        domainEvents.size() == 1
        domainEvents.first() as LoanInstallmentPaidOff.LoanInstallmentUnpaid
        loan.getInstallments().size() == installmentsNumberBefore + 1
        loan.nextInstallmentDate == LocalDate.now().plusMonths(1)
    }

    def "should not paid off fully next installment when borrower balance is not enough"() {
        given:
        Loan loan = new Loan(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now(), 2, LoanStatus.ACTIVE, new ArrayList<LoanInstallment>())
        def availableBalance = 7 as BigDecimal
        AccountInfo accountInfo = new AccountInfo(availableBalance, BigDecimal.ZERO)

        when:
        def domainEvents = loan.payOffNextInstallment(accountInfo)

        then:
        domainEvents.size() == 3
        retrieveLoanInstallmentUnpaidEventFrom(domainEvents)
        retrieveLoanInstallmentPaidOffToLenderEventFrom(domainEvents).amount == 5
        retrieveSystemFeeChargedOnLoanEventFrom(domainEvents).amount == 2
        loan.getInstallments().size() == 1
        loan.nextInstallmentDate == LocalDate.now().plusMonths(1)
    }

    def "should paid off fully next installment when borrower balance is enough"() {
        given:
        Loan loan = new Loan(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now(), 2, LoanStatus.ACTIVE, new ArrayList<LoanInstallment>())
        def availableBalance = 10 as BigDecimal
        AccountInfo accountInfo = new AccountInfo(availableBalance, BigDecimal.ZERO)

        when:
        def domainEvents = loan.payOffNextInstallment(accountInfo)

        then:
        domainEvents.size() == 2
        retrieveLoanInstallmentPaidOffToLenderEventFrom(domainEvents).amount == 5
        retrieveSystemFeeChargedOnLoanEventFrom(domainEvents).amount == 5
        loan.getInstallments().size() == 1
        loan.nextInstallmentDate == LocalDate.now().plusMonths(1)
    }

    def "should finish loan when next installment is paid off and there is no more future installments"() {
        given:
        Loan loan = new Loan(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 2 as BigDecimal, 2 as BigDecimal,
                LocalDateTime.now(), LocalDate.now(), 2, LoanStatus.ACTIVE, new ArrayList<LoanInstallment>(Arrays.asList(LoanDataFixtures.PAID_LOAN_INSTALLMENT)))
        def installmentsNumberBefore = loan.getInstallments().size()
        def availableBalance = 10 as BigDecimal
        AccountInfo accountInfo = new AccountInfo(availableBalance, BigDecimal.ZERO)

        when:
        def domainEvents = loan.payOffNextInstallment(accountInfo)

        then:
        domainEvents.size() == 3
        retrieveLoanInstallmentPaidOffToLenderEventFrom(domainEvents).amount == 1
        retrieveSystemFeeChargedOnLoanEventFrom(domainEvents).amount == 1
        retrieveLoanFinishedEventFrom(domainEvents)
        loan.getInstallments().size() == installmentsNumberBefore + 1
    }

    def "should not paid off any unpaid installments when borrower balance is empty"() {
        given:
        Loan loan = LoanDataFixtures.loanWithUnpaidInstallmentFor(UUID.randomUUID(), UUID.randomUUID())
        AccountInfo accountInfo = new AccountInfo(BigDecimal.ZERO, BigDecimal.ZERO)

        when:
        def domainEvents = loan.payForUnpaidInstallments(accountInfo)

        then:
        domainEvents.size() == 0
    }

    def "should paid off unpaid installment when borrower balance is enough"() {
        given:
        Loan loan = LoanDataFixtures.loanWithUnpaidInstallmentFor(UUID.randomUUID(), UUID.randomUUID())
        def availableBalance = 2 as BigDecimal
        AccountInfo accountInfo = new AccountInfo(availableBalance, BigDecimal.ZERO)

        when:
        def domainEvents = loan.payForUnpaidInstallments(accountInfo)

        then:
        domainEvents.size() == 2
        retrieveLoanInstallmentPaidOffToLenderEventFrom(domainEvents).amount == 1
        retrieveSystemFeeChargedOnLoanEventFrom(domainEvents).amount == 1
    }

    def "should paid off part of unpaid installment when borrower balance is not enough"() {
        given:
        def loanInstallment = new LoanInstallment(
                UUID.randomUUID(), BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO, LocalDate.now(), LocalDate.now(), LoanInstallment.InstallmentStatus.UNPAID)
        Loan loan = new Loan(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, BigDecimal.TEN,
                LocalDateTime.now(), LocalDate.now(), 10, LoanStatus.ACTIVE, new ArrayList<LoanInstallment>(Arrays.asList(loanInstallment)))
        AccountInfo accountInfo = new AccountInfo(1 as BigDecimal, BigDecimal.ZERO)

        when:
        def domainEvents = loan.payForUnpaidInstallments(accountInfo)

        then:
        domainEvents.size() == 2
        retrieveLoanInstallmentPaidOffToLenderEventFrom(domainEvents).amount == 1
        retrieveSystemFeeChargedOnLoanEventFrom(domainEvents).amount == 0
    }

    def "should finish loan when all unpaid installments are paid off and there is no more future installments"() {
        given:
        def loanInstallment = new LoanInstallment(
                UUID.randomUUID(), BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO, LocalDate.now(), LocalDate.now(), LoanInstallment.InstallmentStatus.UNPAID)
        Loan loan = new Loan(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 1 as BigDecimal, 1 as BigDecimal,
                LocalDateTime.now(), LocalDate.now(), 1, LoanStatus.ACTIVE, new ArrayList<LoanInstallment>(Arrays.asList(loanInstallment)))
        def availableBalance = 2 as BigDecimal
        AccountInfo accountInfo = new AccountInfo(availableBalance, BigDecimal.ZERO)

        when:
        def domainEvents = loan.payForUnpaidInstallments(accountInfo)

        then:
        domainEvents.size() == 3
        retrieveLoanInstallmentPaidOffToLenderEventFrom(domainEvents).amount == 1
        retrieveSystemFeeChargedOnLoanEventFrom(domainEvents).amount == 1
        retrieveLoanFinishedEventFrom(domainEvents)
    }

    def "should return calculated installment amount for borrower"() {
        given:
        Loan loan = LoanDataFixtures.loanWithPaidInstallmentFor(UUID.randomUUID(), UUID.randomUUID())

        when:
        def amount = loan.calculatedInstallmentAmount()

        then:
        amount == (loan.getSystemFee() + loan.getAmountToRepaidToLender())/loan.getInstallmentsNumber()
    }

    def "should return all installments made so far"() {
        given:
        Loan loan = LoanDataFixtures.loanWithPaidInstallmentFor(UUID.randomUUID(), UUID.randomUUID())

        when:
        def installments = loan.getInstallments()

        then:
        installments.size() == 1
        with(installments.first()) {
            repaymentAmount == LoanDataFixtures.PAID_LOAN_INSTALLMENT.totalRepaymentAmount()
            timelyRepaymentTime == LoanDataFixtures.PAID_LOAN_INSTALLMENT.timelyRepaymentTime
            repaymentTime == LoanDataFixtures.PAID_LOAN_INSTALLMENT.repaymentTime
            status == LoanDataFixtures.PAID_LOAN_INSTALLMENT.status.name()
        }
    }

    LoanInstallmentPaidOff.LoanInstallmentUnpaid retrieveLoanInstallmentUnpaidEventFrom(List<DomainEvent> domainEvents) {
        domainEvents.stream().filter { n -> n instanceof LoanInstallmentPaidOff.LoanInstallmentUnpaid }
                .map { n -> (LoanInstallmentPaidOff.LoanInstallmentUnpaid) n }.findFirst().get()
    }

    LoanInstallmentPaidOff.LoanInstallmentPaidOffToLender retrieveLoanInstallmentPaidOffToLenderEventFrom(List<DomainEvent> domainEvents) {
        domainEvents.stream().filter { n -> n instanceof LoanInstallmentPaidOff.LoanInstallmentPaidOffToLender }
                .map { n -> (LoanInstallmentPaidOff.LoanInstallmentPaidOffToLender) n }.findFirst().get()
    }

    LoanInstallmentPaidOff.SystemFeeChargedOnLoan retrieveSystemFeeChargedOnLoanEventFrom(List<DomainEvent> domainEvents) {
        domainEvents.stream().filter { n -> n instanceof LoanInstallmentPaidOff.SystemFeeChargedOnLoan }
                .map { n -> (LoanInstallmentPaidOff.SystemFeeChargedOnLoan) n }.findFirst().get()
    }

    LoanFinished retrieveLoanFinishedEventFrom(List<DomainEvent> domainEvents) {
        domainEvents.stream().filter { n -> n instanceof LoanFinished }
                .map { n -> (LoanFinished) n }.findFirst().get()
    }
}
