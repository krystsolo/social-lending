package pl.fintech.dragons.dragonslending.tests

import groovy.json.JsonSlurper
import io.restassured.RestAssured
import io.restassured.specification.RequestSpecification
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import pl.fintech.dragons.dragonslending.FictureDataFactory
import pl.fintech.dragons.dragonslending.PostgreSQLContainerSpecificationFT
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionService
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.Auction
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionRepository
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionStatus
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import pl.fintech.dragons.dragonslending.sociallending.identity.application.web.UserRegisterRequest
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.User
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRepository
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade

import java.math.RoundingMode

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("functional-test")
@EnableAutoConfiguration
class AuctionFT extends PostgreSQLContainerSpecificationFT {

    @LocalServerPort
    int serverPort

    @Autowired
    AuctionRepository auctionRepository

    @Autowired
    AuctionService auctionService

    @Autowired
    UserRepository userRepository

    RequestSpecification restClient

    static UserRegisterRequest userRegister

    def setup() {
        def jsonSlurper = new JsonSlurper()

        auctionRepository.deleteAll()

        restClient = RestAssured.given()
                .port(serverPort)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .log().all()

        if(userRegister == null) {
            userRegister = new UserRegisterRequest("test@test.pl", "test", "test", "test", "test")
            restClient.with()
                    .body(userRegister)
                    .when().post("/api/users/sign-up").then().statusCode(201)
        }

        String token = restClient.with()
                .body(jsonSlurper.parseText('''
                { "email": "test@test.pl",
                  "password": "test"
                }'''))
                .when().post("/api/login")
                .then().statusCode(200)
                .extract().header("X-Authorization")

        restClient = restClient
                .header("X-Authorization", token)

    }

    def "Should save new auction in database and return UUID"() {
        when:
        def response = restClient.with().body(FictureDataFactory.AUCTION_REQUEST).when().post('/api/auctions')

        then:
        response.statusCode() == 201
        and:
        response.body().as(UUID.class) != null
    }

    def "Should delete auction from database"() {
        given:
        UUID userID = userRepository.getPrincipalFor("test@test.pl").id
        UUID auctionUUID = auctionRepository.save(FictureDataFactory.auction(userID)).id

        when:
        def response = restClient.when().delete('/api/auctions/' + auctionUUID)
        def fromDb = auctionRepository.findAllByAuctionStatus(AuctionStatus.ACTIVE)

        then:
        response.statusCode() == 204
        and:
        fromDb.empty
    }

    def "Should return list of all auctions" () {
        given:
        UUID userID = userRepository.getPrincipalFor("test@test.pl").id
        Auction auction = auctionRepository.save(FictureDataFactory.auction(userID))
        Auction auction1 = auctionRepository.save(FictureDataFactory.auction(userID))

        List<AuctionQueryDto> auctionQueryList= [auction.toAuctionDto("test"), auction1.toAuctionDto("test")]

        when:
        def response = restClient.when().get('/api/auctions/public')

        then:
        response.statusCode() == 200
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class).size() == 2
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class) == auctionQueryList
    }

    def "Should return list of all auctions without current logged user" () {
        given:
        UUID userID = userRepository.getPrincipalFor("test@test.pl").id
        User user2 = userRepository.save(new User("test2@test.pl", "test2", "test", "test", "test"))
        Auction auction = auctionRepository.save(FictureDataFactory.auction(user2.id))
        auctionRepository.save(FictureDataFactory.auction(userID))

        List<AuctionQueryDto> auctionQueryList= [auction.toAuctionDto("test2")]

        when:
        def response = restClient.when().get('/api/auctions')

        then:
        response.statusCode() == 200
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class).size() == 1
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class) == auctionQueryList
    }

    def "Should return list of auctions current logged user" () {
        given:
        UUID userID = userRepository.getPrincipalFor("test@test.pl").id
        User user2 = userRepository.save(new User("test3@test.pl", "test3", "test", "test", "test"))
        Auction auction = auctionRepository.save(FictureDataFactory.auction(userID))
        auctionRepository.save(FictureDataFactory.auction(user2.id))

        List<AuctionQueryDto> auctionQueryList= [auction.toAuctionDto("test")]

        when:
        def response = restClient.when().get('/api/auctions?yours=true')

        then:
        response.statusCode() == 200
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class).size() == 1
        and:
        response.body().jsonPath().getList(".", AuctionQueryDto.class) == auctionQueryList
    }
}
