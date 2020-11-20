package pl.fintech.dragons.dragonslending.paymentplatformmock.client

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ContextConfiguration
import pl.fintech.dragons.dragonslending.WireMockInitializer
import spock.lang.Specification
import spock.lang.Subject

import static com.github.tomakehurst.wiremock.client.WireMock.*

@ContextConfiguration(
        classes = [BankClientConfig],
        initializers = [WireMockInitializer]
)
class BankClientIT extends Specification {

    @Autowired
    WireMockServer wireMockServer

    @Subject
    @Autowired
    BankClient bankClient

    def "POST /transactions to Bank API should request transaction in bank"() {
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
        bankClient.requestTransaction(new BankClient.BankTransaction(sourceAccountNumber, targetAccountNumber, amount))
    }

}