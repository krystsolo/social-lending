package pl.fintech.dragons.dragonslending.paymentplatformmock

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.paymentplatformmock.client.BankClient
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.AccountFinder
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.SystemAccountNumber
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade
import spock.lang.Specification

class PaymentServiceTest extends Specification {

    private BankClient bankClient = Mock(BankClient)
    private EventPublisher eventPublisher = Mock(EventPublisher)
    private AccountFinder accountFinder = Mock(AccountFinder)
    private AuthenticationFacade authenticationFacade = Mock(AuthenticationFacade)

    private PaymentService paymentService = new PaymentService(bankClient, eventPublisher, accountFinder, authenticationFacade)

    def "should register new deposit to system account"() {
        given:
        UUID systemAccountNumber = UUID.randomUUID()
        UUID currentUserId = UUID.randomUUID()
        UUID from = UUID.randomUUID()
        BigDecimal amount = BigDecimal.TEN
        DepositRequest depositRequest = new DepositRequest(from, amount)

        accountFinder.getSystemAccount() >> SystemAccountNumber.of(systemAccountNumber)
        authenticationFacade.idOfCurrentLoggedUser() >> currentUserId

        when:
        paymentService.registerDeposit(depositRequest)

        then:
        1 * bankClient.requestTransaction(new BankClient.BankTransaction(from, systemAccountNumber, amount))
        1 * eventPublisher.publish(
                {
                    it.userId == currentUserId &&
                    it.sourceAccountNumber == from &&
                    it.targetAccountNumber == systemAccountNumber &&
                    it.amount == amount
                } as MoneyDepositedFromExternalSource
        )
    }
}
