package pl.fintech.dragons.dragonslending.sociallending.payment.account.application

import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRegistered
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.Account
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountRepository
import spock.lang.Specification

class UserRegisteredEventHandlerTest extends Specification {

    AccountRepository accountRepository = Mock(AccountRepository)
    UserRegisteredEventHandler userRegisteredEventHandler = new UserRegisteredEventHandler(accountRepository)
    def "should create new account when UserRegistered event received"() {
        given:
        UserRegistered userRegistered = UserRegistered.now(UUID.randomUUID())

        when:
        userRegisteredEventHandler.handle(userRegistered)

        then:
        1 * accountRepository.save(_ as Account)
    }
}
