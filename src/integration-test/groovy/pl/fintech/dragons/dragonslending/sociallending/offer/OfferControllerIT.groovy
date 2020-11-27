package pl.fintech.dragons.dragonslending.sociallending.offer

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
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionDataFictureFactory
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferQueryDto
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
                OfferController.class
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("integration-test")
class OfferControllerIT extends Specification {

    @LocalServerPort
    int serverPort

    @Autowired
    OfferService mockedOfferService

    RequestSpecification restClient

    def setup() {
        restClient = RestAssured.given()
                .port(serverPort)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .log().all()
    }

    def 'GET /api/offers/user should return HTTP 200 with list of offers current logged user'() {
        given:
        mockedOfferService.getCurrentLoggedUserOffers() >> OfferDataFictureFactory.OFFER_QUERY_LIST

        when:
        def response = restClient.when().get('/api/offers/user')

        then:
        response.statusCode() == 200
        and:
        response.body().jsonPath().getList(".", OfferQueryDto.class).size() == 2
        and:
        response.body().jsonPath().getList(".", OfferQueryDto.class) == OfferDataFictureFactory.OFFER_QUERY_LIST
    }

    def 'GET /api/offers/auction/{id} should return HTTP 200 with all offers by auction id'() {
        given:
        mockedOfferService.getOffersByAuctionId(AuctionDataFictureFactory.AUCTION_ID) >> OfferDataFictureFactory.OFFER_QUERY_LIST

        when:
        def response = restClient.when().get('/api/offers/auction/' + AuctionDataFictureFactory.AUCTION_ID)

        then:
        response.statusCode() == 200
        and:
        response.body().jsonPath().getList(".", OfferQueryDto.class).size() == 2
        and:
        response.body().jsonPath().getList(".", OfferQueryDto.class) == OfferDataFictureFactory.OFFER_QUERY_LIST
    }

    def 'POST /api/offers should return HTTP 201 with offer id'() {
        given:
        mockedOfferService.saveOffer(OfferDataFictureFactory.OFFER_REQUEST) >> OfferDataFictureFactory.OFFER_ID

        when:
        def response = restClient.with().body(OfferDataFictureFactory.OFFER_REQUEST).when().post('/api/offers')

        then:
        response.statusCode() == 201
        and:
        response.body().as(UUID.class) == OfferDataFictureFactory.OFFER_ID
    }

    def 'DELETE /api/offers/{id} should return HTTP 200'() {
        when:
        def response = restClient.when().delete('/api/offers/' + OfferDataFictureFactory.OFFER_ID)

        then:
        response.statusCode() == 204
    }

    @Unroll
    def 'POST /api/offers should return HTTP 400 and not save new offer because of invalid offer data: #userMessage'() {
        when:
        def response = restClient.with().body(request).when().post('/api/offers')

        then:
        response.statusCode() == 400

        where:
        request                                                                          || userMessage
        OfferDataFictureFactory.OFFER_REQUEST.withOfferAmount(null)                      || 'Offer amount cannot be null.'
        OfferDataFictureFactory.OFFER_REQUEST.withOfferAmount(BigDecimal.valueOf(-1))    || 'Offer amount value should not be less then 0.'
        OfferDataFictureFactory.OFFER_REQUEST.withOfferAmount(BigDecimal.valueOf(10001)) || 'Offer amount value should not be greater than 10000.'
        OfferDataFictureFactory.OFFER_REQUEST.withInterestRate(null)                     || 'Interest rate cannot be null.'
        OfferDataFictureFactory.OFFER_REQUEST.withInterestRate(-1)                       || 'Interest rate value should not be less than 0.'
        OfferDataFictureFactory.OFFER_REQUEST.withInterestRate(21)                       || 'Interest rate value should not be greater than 20.'
        OfferDataFictureFactory.OFFER_REQUEST.withAuctionId(null)                        || 'Auction id cannot be null.'
    }

    @TestConfiguration
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        OfferService offerService() {
            return detachedMockFactory.Stub(OfferService)
        }
    }
}
