package pl.fintech.dragons.dragonslending.sociallending.payment.account.application

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.BankApiService
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.MoneyTransferEvent

import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade
import spock.lang.Specification

import static pl.fintech.dragons.dragonslending.sociallending.payment.account.AccountFixtures.SYSTEM_ACCOUNT_NUMBER
import static pl.fintech.dragons.dragonslending.sociallending.payment.account.AccountFixtures.ACCOUNT
import static pl.fintech.dragons.dragonslending.sociallending.payment.account.AccountFixtures.USER_ID

class WithdrawMoneyCommandHandlerTest extends Specification {

    AccountRepository accountRepository = Mock(AccountRepository)
    EventPublisher eventPublisher = Mock(EventPublisher)
    AuthenticationFacade authenticationFacade = Mock(AuthenticationFacade)
    BankApiService bankApiService = Mock(BankApiService)
    WithdrawMoneyCommandHandler withdrawMoneyCommandHandler = new WithdrawMoneyCommandHandler(
            accountRepository, eventPublisher, authenticationFacade, bankApiService)

    def "should withdraw money from account and publish event"() {
        given:
        UUID requestedAccountNumber = UUID.randomUUID()
        BigDecimal amountToWithdraw = BigDecimal.TEN
        authenticationFacade.idOfCurrentLoggedUser() >> USER_ID
        accountRepository.getOne(USER_ID) >> ACCOUNT
        accountRepository.getSystemAccountNumber() >> SYSTEM_ACCOUNT_NUMBER

        when:
        withdrawMoneyCommandHandler.withdraw(requestedAccountNumber, amountToWithdraw)

        then:
        1 * bankApiService.requestWithdraw(SYSTEM_ACCOUNT_NUMBER.number(), requestedAccountNumber, amountToWithdraw)
        1 * eventPublisher.publish(_ as MoneyTransferEvent.MoneyWithdrawn)
    }
}
