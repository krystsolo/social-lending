package pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.bankapi

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.fintech.dragons.dragonslending.WireMockInitializer
import spock.lang.Specification
import spock.lang.Subject

import static com.github.tomakehurst.wiremock.client.WireMock.*

@ContextConfiguration(
        classes = [BankApiConfig],
        initializers = [WireMockInitializer, ConfigFileApplicationContextInitializer]
)
@ActiveProfiles("integration-test")
class BankApiIT extends Specification {

    @Autowired
    WireMockServer wireMockServer

    @Subject
    @Autowired
    BankApi bankApi

    def "GET /accounts/{accountNumber} from bankApi should return body with AccountBalance and OK status"() {
        given:
        UUID accountNumber = UUID.fromString("c425aedc-f260-4a5c-81e0-8da3c124710c")
        BankApi.Account account = new BankApi.Account(accountNumber, 'id', BigDecimal.valueOf(250))
        wireMockServer.stubFor(
                get('/accounts/' + accountNumber)
                        .willReturn(ok()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("bankapi/get-account-response.json"))
    )

        when:
        BankApi.Account response = bankApi.getAccount(accountNumber)

        then:
        response == account
    }

    def "POST /accounts to BankApi should create new account for user and return id of this account"() {
        given:
        UUID accountNumber = UUID.randomUUID()
        String userId = "userId"
        String requestBody = "{\"name\": \"$userId\"}"
        def locationHeader = 'https://localhost.pl/accounts/' + accountNumber

        wireMockServer.stubFor(
                post('/accounts').withRequestBody(equalToJson(requestBody))
                        .willReturn(created().
                                withHeader(HttpHeaders.LOCATION, locationHeader)))

        expect:
        bankApi.createAccount(new BankApi.UserId(userId))
    }

    def "POST /transactions to bankApi should request transaction in bank"() {
        given:
        UUID sourceAccountNumber = UUID.randomUUID()
        UUID targetAccountNumber = UUID.randomUUID()
        BigDecimal amount = BigDecimal.TEN
        String requestBody =
                "{ \
                    \"sourceAccountNumber\": \"$sourceAccountNumber\", \
                    \"targetAccountNumber\": \"$targetAccountNumber\", \
                    \"amount\": $amount \
                 }"

        wireMockServer.stubFor(
                post('/transactions').withRequestBody(equalToJson(requestBody))
                        .willReturn(created()))

        expect:
        bankApi.requestTransaction(new BankApi.BankTransaction(sourceAccountNumber, targetAccountNumber, amount))
    }

}
