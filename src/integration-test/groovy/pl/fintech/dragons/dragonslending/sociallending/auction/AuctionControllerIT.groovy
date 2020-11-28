package pl.fintech.dragons.dragonslending.sociallending.auction

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
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto
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
                AuctionController.class
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("integration-test")
class AuctionControllerIT extends Specification {

    @LocalServerPort
    int serverPort

    @Autowired
    AuctionService mockedAuctionService

    RequestSpecification restClient

    def setup() {
        restClient = RestAssured.given()
                .port(serverPort)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .log().all()
    }

    def 'GET /api/auctions/{id} should return HTTP 200 with auction'() {
        given:
        mockedAuctionService.getAuction(AuctionDataFictureFactory.AUCTION_ID) >> AuctionDataFictureFactory.AUCTION_QUERY_DTO

        when:
        def response = restClient.when().get('/api/auctions/' + AuctionDataFictureFactory.AUCTION_ID)

        then:
        response.statusCode() == 200
        and:
        response.body().as(AuctionQueryDto) == AuctionDataFictureFactory.AUCTION_QUERY_DTO
    }

    def 'GET /api/auctions should return HTTP 200 with auction without current logged user'() {
        given:
        mockedAuctionService.getAllNotCurrentUserAuctions() >> AuctionDataFictureFactory.AUCTION_QUERY_LIST

        when:
        def response = restClient.when().get('/api/auctions')

        then:
        response.statusCode() == 200
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class).size() == 2
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class) == AuctionDataFictureFactory.AUCTION_QUERY_LIST
    }

    def 'GET /api/auctions?yours=true should return HTTP 200 with current logged user auctions'() {
        given:
        mockedAuctionService.getCurrentUserAuctions() >> AuctionDataFictureFactory.AUCTION_QUERY_LIST

        when:
        def response = restClient.when().get('/api/auctions?yours=true')

        then:
        response.statusCode() == 200
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class).size() == 2
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class) == AuctionDataFictureFactory.AUCTION_QUERY_LIST
    }

    def 'GET /api/auctions/public should return HTTP 200 with all auctions'() {
        given:
        mockedAuctionService.getPublicAuctions() >> AuctionDataFictureFactory.AUCTION_QUERY_LIST

        when:
        def response = restClient.when().get('/api/auctions/public')

        then:
        response.statusCode() == 200
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class).size() == 2
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class) == AuctionDataFictureFactory.AUCTION_QUERY_LIST
    }

    def 'POST /api/auctions should return HTTP 201 with auction id'() {
        given:
        mockedAuctionService.saveAuction(AuctionDataFictureFactory.AUCTION_REQUEST) >> AuctionDataFictureFactory.AUCTION_ID

        when:
        def response = restClient.with().body(AuctionDataFictureFactory.AUCTION_REQUEST).when().post('/api/auctions')

        then:
        response.statusCode() == 201
        and:
        response.body().as(UUID.class) == AuctionDataFictureFactory.AUCTION_ID
    }

    def 'PUT /api/auctions/{id} should return HTTP 200 with auction id'() {
        given:
        mockedAuctionService.updateAuction(AuctionDataFictureFactory.AUCTION_REQUEST, AuctionDataFictureFactory.AUCTION_ID) >> AuctionDataFictureFactory.AUCTION_ID

        when:
        def response = restClient.with().body(AuctionDataFictureFactory.AUCTION_REQUEST).when().put('/api/auctions/' + AuctionDataFictureFactory.AUCTION_ID)

        then:
        response.statusCode() == 200
        and:
        response.body().as(UUID.class) == AuctionDataFictureFactory.AUCTION_ID
    }

    def 'DELETE /api/auctions/{id} should return HTTP 204'() {
        when:
        def response = restClient.when().delete('/api/auctions/' + AuctionDataFictureFactory.AUCTION_ID)

        then:
        response.statusCode() == 204
        and:
        1 * mockedAuctionService.deleteAuction(AuctionDataFictureFactory.AUCTION_ID)
    }

    @Unroll
    def 'POST /api/auctions should return HTTP 400 and not save new auction because of invalid auction data: #userMessage'() {
        when:
        def response = restClient.with().body(request).when().post('/api/auctions')

        then:
        response.statusCode() == 400

        where:
        request                                                                             || userMessage
        AuctionDataFictureFactory.AUCTION_REQUEST.withLoanAmount(null)                      || 'Loan amount cannot be null.'
        AuctionDataFictureFactory.AUCTION_REQUEST.withLoanAmount(BigDecimal.valueOf(-1))    || 'Loan amount value should not be less then 0.'
        AuctionDataFictureFactory.AUCTION_REQUEST.withLoanAmount(BigDecimal.valueOf(10001)) || 'Loan amount value should not be greater than 10000.'
        AuctionDataFictureFactory.AUCTION_REQUEST.withTimePeriod(null)                      || 'Time period cannot be null.'
        AuctionDataFictureFactory.AUCTION_REQUEST.withTimePeriod(0)                         || 'Time period value should not be less than 1.'
        AuctionDataFictureFactory.AUCTION_REQUEST.withTimePeriod(37)                        || 'Time period value should not be greater than 36.'
        AuctionDataFictureFactory.AUCTION_REQUEST.withInterestRate(null)                    || 'Interest rate cannot be null.'
        AuctionDataFictureFactory.AUCTION_REQUEST.withInterestRate(-1)                      || 'Interest rate value should not be less than 0.'
        AuctionDataFictureFactory.AUCTION_REQUEST.withInterestRate(21)                      || 'Interest rated value should not be greater than 20.'
        AuctionDataFictureFactory.AUCTION_REQUEST.withEndDate(null)                         || 'End date cannot be null.'
    }

    @TestConfiguration
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        AuctionService auctionService() {
            return detachedMockFactory.Stub(AuctionService)
        }
    }
}
