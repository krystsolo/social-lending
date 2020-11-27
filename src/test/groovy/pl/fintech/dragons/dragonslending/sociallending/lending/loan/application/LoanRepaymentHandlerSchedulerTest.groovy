package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanDataFixtures
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanRepository
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.AccountFinder
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountInfo
import spock.lang.Specification

class LoanRepaymentHandlerSchedulerTest extends Specification {
    LoanRepository loanRepository = Mock(LoanRepository)
    AccountFinder accountFinder = Mock(AccountFinder)
    EventPublisher eventPublisher = Mock(EventPublisher)
    LoanRepaymentHandlerScheduler loanRepaymentHandlerScheduler = new LoanRepaymentHandlerScheduler(loanRepository, accountFinder, eventPublisher)

    def "should process repayments for loans with unpaid installments"() {
        given:
        def borrowerId = UUID.randomUUID()
        loanRepository.findAllLoansWithUnpaidInstallments() >> [LoanDataFixtures.loanWithUnpaidInstallmentFor(borrowerId, UUID.randomUUID())]
        loanRepository.findAllLoansWithTodayAsNextInstallmentDay() >> []
        AccountInfo accountInfo = new AccountInfo(BigDecimal.ZERO, BigDecimal.ZERO)

        when:
        loanRepaymentHandlerScheduler.handleRepaymentsOfLoanInstallments()

        then:
        1 * accountFinder.getAccountInfoFor(borrowerId) >> accountInfo
        1 * eventPublisher.publish(_)
    }

    def "should process repayments for loans with today as next installment date"() {
        given:
        def borrowerId = UUID.randomUUID()
        loanRepository.findAllLoansWithTodayAsNextInstallmentDay() >> [LoanDataFixtures.loanWithTodayAsNextInstallment(borrowerId, UUID.randomUUID())]
        loanRepository.findAllLoansWithUnpaidInstallments() >> []
        def accountInfo = new AccountInfo(BigDecimal.ZERO, BigDecimal.ZERO)

        when:
        loanRepaymentHandlerScheduler.handleRepaymentsOfLoanInstallments()

        then:
        1 * accountFinder.getAccountInfoFor(borrowerId) >> accountInfo
        1 * eventPublisher.publish(_)
    }
}
