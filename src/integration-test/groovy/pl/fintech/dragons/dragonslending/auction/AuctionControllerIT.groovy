
package pl.fintech.dragons.dragonslending.auction

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
import pl.fintech.dragons.dragonslending.auction.dto.AuctionQueryDto
import spock.lang.Specification
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
        mockedAuctionService.getAuction(AuctionData.AUCTION_ID) >> AuctionData.AUCTION_QUERY_DTO

        when:
        def response = restClient.when().get('/api/auctions/' + AuctionData.AUCTION_ID)

        then:
        response.statusCode() == 200
        and:
        response.body().as(AuctionQueryDto) == AuctionData.AUCTION_QUERY_DTO
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
