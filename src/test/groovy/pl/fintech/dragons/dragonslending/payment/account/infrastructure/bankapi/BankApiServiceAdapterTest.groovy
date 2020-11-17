package pl.fintech.dragons.dragonslending.payment.account.infrastructure.bankapi

import org.springframework.http.ResponseEntity
import spock.lang.Specification

class BankApiServiceAdapterTest extends Specification {

    private BankApi api = Mock(BankApi)
    private BankApiServiceAdapter bankApiServiceAdapter = new BankApiServiceAdapter(api)

    def "should retrieve id of newly created account from header response from bank api"() {
        given:
        def id = 'id'
        UUID uuid = UUID.randomUUID()
        api.createAccount(new BankApi.UserId(id)) >> ResponseEntity.created(new URI('https://localhost.pl/api/created/' + uuid)).build()

        when:
        def account = bankApiServiceAdapter.createAccountFor(id)

        then:
        account == uuid
    }
}
