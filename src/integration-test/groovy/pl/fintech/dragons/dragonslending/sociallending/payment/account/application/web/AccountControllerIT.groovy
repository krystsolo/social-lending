package pl.fintech.dragons.dragonslending.sociallending.payment.account.application.web

import io.restassured.RestAssured
import io.restassured.specification.RequestSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.AccountFinder
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.WithdrawMoneyCommandHandler
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountInfo
import spock.lang.Specification
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@SpringBootTest(
        classes = [
                StubConfig.class,
                DispatcherServletAutoConfiguration.class,
                ServletWebServerFactoryAutoConfiguration.class,
                WebEndpointAutoConfiguration.class,
                EndpointAutoConfiguration.class,

        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("integration-test")
class AccountControllerIT extends Specification {

    @LocalServerPort
    int serverPort

    @Autowired
    AccountFinder accountFinder

    @Autowired
    WithdrawMoneyCommandHandler withdrawMoneyCommandHandler

    RequestSpecification restClient

    def setup() {
        restClient = RestAssured.given()
                .port(serverPort)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .log().all()
    }

    def 'GET /api/account/self should return HTTP 200 with current logged user account info'() {
        given:
        AccountInfo accountInfo = new AccountInfo(100 as BigDecimal, 50 as BigDecimal)
        accountFinder.getAccountInfoOfCurrentLoggedUser() >> accountInfo

        when:
        def response = restClient.when().get('/api/account/self')

        then:
        response.statusCode() == 200
        and:
        (response.body().as(AccountInfo)).with {
            balance == accountInfo.balance
            availableFunds == accountInfo.availableFunds
        }
    }

    def 'POST /api/account/withdraw should register new withdraw from user account and return HTTP 200'() {
        given:
        WithdrawRequest request = new WithdrawRequest(UUID.randomUUID(), 100 as BigDecimal)

        when:
        def response = restClient.with().body(request).when().post('/api/account/withdraw')

        then:
        response.statusCode() == 200
        and:
        1 * withdrawMoneyCommandHandler.withdraw(request.requestedAccountNumber, request.amount)
    }

    @Unroll
    def 'POST /api/account/withdraw should not register new withdraw from user account because of invalid request body: #userMessage'() {
        when:
        def response = restClient.with().body(request).when().post('/api/account/withdraw')

        then:
        response.statusCode() == 400

        where:
        request                                   || userMessage
        new WithdrawRequest(null, 100 as BigDecimal)     || 'RequestedAccountNumber cannot be null.'
        new WithdrawRequest(UUID.randomUUID(), 0 as BigDecimal)       || 'Amount must be at least equal or bigger than 1'
        new WithdrawRequest(UUID.randomUUID(), -1 as BigDecimal)  || 'Amount cannot be negative number'
    }

    @TestConfiguration
    @ComponentScan
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        AccountFinder accountFinder() {
            return detachedMockFactory.Stub(AccountFinder)
        }

        @Bean
        WithdrawMoneyCommandHandler withdrawMoneyCommandHandler() {
            return detachedMockFactory.Mock(WithdrawMoneyCommandHandler)
        }
    }
}