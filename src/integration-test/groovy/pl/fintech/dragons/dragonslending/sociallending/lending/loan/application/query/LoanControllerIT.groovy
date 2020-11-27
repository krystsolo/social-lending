package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query

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
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import java.time.LocalDate
import java.time.LocalDateTime

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
class LoanControllerIT extends Specification {

    @LocalServerPort
    int serverPort

    @Autowired
    LoanFinder loanFinder

    RequestSpecification restClient

    def setup() {
        restClient = RestAssured.given()
                .port(serverPort)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .log().all()
    }

    def 'GET /api/loans/self should return HTTP 200 with current logged user loans'() {
        given:
        LoanQuery loanQuery = new LoanQuery(UUID.randomUUID(), "username", BigDecimal.TEN, LocalDateTime.now(), LocalDate.now(), 10)
        LoansView loansView = new LoansView(Arrays.asList(loanQuery), Collections.emptyList())
        loanFinder.getLoansForCurrentUser() >> loansView

        when:
        def response = restClient.when().get('/api/loans/self')

        then:
        response.statusCode() == 200
        and:
        response.body().as(LoansView) == loansView
    }

    def 'GET /api/loans/{id}} should return HTTP 200 with loans details of specific id'() {
        given:
        def id = UUID.randomUUID()
        LoanDetailsView loanDetailsView = new LoanDetailsView(id, "username", LoanDetailsView.LoanType.GRANTED, LocalDateTime.now(), LocalDate.now(), BigDecimal.TEN, Collections.emptyList())
        loanFinder.getLoanDetailsFor(id) >> loanDetailsView

        when:
        def response = restClient.when().get('/api/loans/' + id)

        then:
        response.statusCode() == 200
        and:
        response.body().as(LoanDetailsView) == loanDetailsView
    }


    @TestConfiguration
    @ComponentScan
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        LoanFinder loanFinder() {
            return detachedMockFactory.Stub(LoanFinder)
        }
    }
}