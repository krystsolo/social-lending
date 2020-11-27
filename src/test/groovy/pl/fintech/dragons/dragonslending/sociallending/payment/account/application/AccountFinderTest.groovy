package pl.fintech.dragons.dragonslending.sociallending.payment.account.application

import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.SystemAccountNumber
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade
import spock.lang.Specification

class AccountFinderTest extends Specification {
    AccountRepository accountRepository = Mock(AccountRepository)
    AuthenticationFacade authenticationFacade = Mock(AuthenticationFacade)
    AccountFinder accountFinder = new AccountFinder(accountRepository, authenticationFacade)

    def "should retrieve current logged user account from repository"() {
        given:
        UUID userId = UUID.randomUUID()
        authenticationFacade.idOfCurrentLoggedUser() >> userId

        and:
        def account = new Account(userId)
        account.deposit(10 as BigDecimal)
        account.freeze(5 as BigDecimal)
        accountRepository.getOneByUserId(userId) >> account

        when:
        def accountInfoOfCurrentLoggedUser = accountFinder.getAccountInfoOfCurrentLoggedUser()

        then:
        accountInfoOfCurrentLoggedUser.balance == account.balance
        accountInfoOfCurrentLoggedUser.availableFunds == account.availableBalance()
    }

    def "should retrieve system account number"() {
        given:
        SystemAccountNumber systemAccountNumber = new SystemAccountNumber(UUID.randomUUID())
        accountRepository.getSystemAccountNumber() >> systemAccountNumber

        when:
        def account = accountFinder.getSystemAccount()

        then:
        account == systemAccountNumber
    }

    def "should retrieve user account from repository by id"() {
        given:
        UUID userId = UUID.randomUUID()
        def account = new Account(userId)
        account.deposit(10 as BigDecimal)
        account.freeze(5 as BigDecimal)
        accountRepository.getOneByUserId(userId) >> account

        when:
        def accountInfoOfCurrentLoggedUser = accountFinder.getAccountInfoFor(userId)

        then:
        accountInfoOfCurrentLoggedUser.balance == account.balance
        accountInfoOfCurrentLoggedUser.availableFunds == account.availableBalance()
    }
}
