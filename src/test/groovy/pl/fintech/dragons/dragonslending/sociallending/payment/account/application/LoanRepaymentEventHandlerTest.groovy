package pl.fintech.dragons.dragonslending.sociallending.payment.account.application

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanInstallmentPaidOff
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.FrozenMoneyReleased
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.MoneyTransferEvent
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.SystemAccountNumber
import spock.lang.Specification

class LoanRepaymentEventHandlerTest extends Specification {

    AccountRepository accountRepository = Mock(AccountRepository)
    EventPublisher eventPublisher = Mock(EventPublisher)

    LoanRepaymentEventHandler loanRepaymentEventHandler = new LoanRepaymentEventHandler(accountRepository, eventPublisher)

    def "should transfer money from borrower account to lender account when loan installment was paid off"() {
        given:
        UUID borrowerId = UUID.randomUUID()
        Account borrowerAccount = new Account(borrowerId)
        borrowerAccount.deposit(BigDecimal.TEN)
        accountRepository.getOneByUserId(borrowerId) >> borrowerAccount

        UUID lenderId = UUID.randomUUID()
        Account lenderAccount = new Account(lenderId)
        accountRepository.getOneByUserId(lenderId) >> lenderAccount

        def paidOffToLender = LoanInstallmentPaidOff.LoanInstallmentPaidOffToLender.now(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN)

        when:
        loanRepaymentEventHandler.handleLoanInstallmentPaidOff(paidOffToLender)

        then:
        1 * eventPublisher.publish(_ as MoneyTransferEvent.LoanInstallmentTransferred)
        borrowerAccount.balance == BigDecimal.ZERO
        lenderAccount.balance == BigDecimal.TEN
    }

    def "should take money from borrower account when there is enough balance but not enough available money when loan installment was paid off"() {
        given:
        UUID borrowerId = UUID.randomUUID()
        Account borrowerAccount = new Account(borrowerId)
        borrowerAccount.deposit(BigDecimal.TEN)
        borrowerAccount.freeze(5 as BigDecimal)
        accountRepository.getOneByUserId(borrowerId) >> borrowerAccount

        UUID lenderId = UUID.randomUUID()
        Account lenderAccount = new Account(lenderId)
        accountRepository.getOneByUserId(lenderId) >> lenderAccount

        def paidOffToLender = LoanInstallmentPaidOff.LoanInstallmentPaidOffToLender.now(UUID.randomUUID(), borrowerId, lenderId, BigDecimal.TEN)

        when:
        loanRepaymentEventHandler.handleLoanInstallmentPaidOff(paidOffToLender)

        then:
        1 * eventPublisher.publish(_ as MoneyTransferEvent.LoanInstallmentTransferred)
        1 * eventPublisher.publish(_ as FrozenMoneyReleased)
    }

    def "should transfer money from borrower account to system account when system fee was paid off"() {
        given:
        UUID borrowerId = UUID.randomUUID()
        Account borrowerAccount = new Account(borrowerId)
        borrowerAccount.deposit(BigDecimal.TEN)
        accountRepository.getOneByUserId(borrowerId) >> borrowerAccount

        def systemAccountNumber = SystemAccountNumber.of(UUID.randomUUID())
        Account systemAccount = new Account(systemAccountNumber.number())
        accountRepository.getOne(systemAccountNumber.number()) >> systemAccount
        accountRepository.getSystemAccountNumber() >> systemAccountNumber

        def paidOffToLender = LoanInstallmentPaidOff.SystemFeeChargedOnLoan.now(UUID.randomUUID(), borrowerId, UUID.randomUUID(), BigDecimal.TEN)

        when:
        loanRepaymentEventHandler.handleSystemFeePaidOff(paidOffToLender)

        then:
        1 * eventPublisher.publish(_ as MoneyTransferEvent.LoanInstallmentTransferred)
        borrowerAccount.balance == BigDecimal.ZERO
        systemAccount.balance == BigDecimal.TEN
    }

    def "should take money from borrower account when there is enough balance but not enough available money when system fee was paid off"() {
        given:
        UUID borrowerId = UUID.randomUUID()
        Account borrowerAccount = new Account(borrowerId)
        borrowerAccount.deposit(BigDecimal.TEN)
        borrowerAccount.freeze(5 as BigDecimal)
        accountRepository.getOneByUserId(borrowerId) >> borrowerAccount

        def systemAccountNumber = SystemAccountNumber.of(UUID.randomUUID())
        Account systemAccount = new Account(systemAccountNumber.number())
        accountRepository.getOne(systemAccountNumber.number()) >> systemAccount
        accountRepository.getSystemAccountNumber() >> systemAccountNumber

        def paidOffToLender = LoanInstallmentPaidOff.SystemFeeChargedOnLoan.now(UUID.randomUUID(), borrowerId, UUID.randomUUID(), BigDecimal.TEN)

        when:
        loanRepaymentEventHandler.handleSystemFeePaidOff(paidOffToLender)

        then:
        1 * eventPublisher.publish(_ as MoneyTransferEvent.LoanInstallmentTransferred)
        borrowerAccount.balance == BigDecimal.ZERO
        systemAccount.balance == BigDecimal.TEN
    }
}
