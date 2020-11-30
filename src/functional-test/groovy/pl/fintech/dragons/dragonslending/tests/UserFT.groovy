package pl.fintech.dragons.dragonslending.tests

import groovy.json.JsonSlurper
import io.restassured.RestAssured
import io.restassured.specification.RequestSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import pl.fintech.dragons.dragonslending.PostgreSQLContainerSpecificationFT
import pl.fintech.dragons.dragonslending.sociallending.identity.application.web.UserRegisterRequest
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRepository

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("functional-test")
@EnableAutoConfiguration
class UserFT extends PostgreSQLContainerSpecificationFT {

    @LocalServerPort
    int serverPort

    @Autowired
    UserRepository userRepository

    RequestSpecification restClient

    def setup() {

        restClient = RestAssured.given()
                .port(serverPort)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .log().all()

    }

    def "Should registry new user"() {
        given:
        UserRegisterRequest userRegister = new UserRegisterRequest("test@test.pl", "test", "test", "test", "test")

        when:
        def response = restClient.with().body(userRegister).when().post("/api/users/sign-up")

        then:
        response.statusCode() == 201
    }

    def "Should login user for account"() {
        given:
        def jsonSlurper = new JsonSlurper()
        def request = '''
                { "email": "test@test.pl",
                  "password": "test"
                }'''

        when:
        def response = restClient.with()
                .body(jsonSlurper.parseText(request))
                .when().post("/api/login")

        then:
        response.statusCode() == 200
    }
}
