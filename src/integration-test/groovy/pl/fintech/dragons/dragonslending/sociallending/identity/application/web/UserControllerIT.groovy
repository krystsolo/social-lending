package pl.fintech.dragons.dragonslending.sociallending.identity.application.web

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
import pl.fintech.dragons.dragonslending.sociallending.identity.UserData
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import spock.lang.Specification
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

import static pl.fintech.dragons.dragonslending.sociallending.identity.UserData.*
import static pl.fintech.dragons.dragonslending.sociallending.identity.application.web.UserController.USER_PATH

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
class UserControllerIT extends Specification {

    @LocalServerPort
    int serverPort

    @Autowired
    UserService mockedUserService

    RequestSpecification restClient

    def setup() {
        restClient = RestAssured.given()
                .port(serverPort)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .log().all()
    }

    def 'GET /api/users/{id} should return HTTP 200 with user'() {
        given:
        UUID id = USER_ID
        UserDto userDto = USER_DTO
        mockedUserService.getUser(id) >> userDto

        when:
        def response = restClient.when().get(USER_PATH + '/' + id)

        then:
        response.statusCode() == 200
        and:
        response.body().as(UserDto) == userDto
    }

    def 'GET /api/users/self should return HTTP 200 with current logged user info'() {
        given:
        UserDto userDto = USER_DTO
        mockedUserService.getCurrentLoggedUser() >> userDto

        when:
        def response = restClient.when().get(USER_PATH + '/self')

        then:
        response.statusCode() == 200
        and:
        response.body().as(UserDto) == userDto
    }

    def 'POST /api/users/sign-up should register new user and return HTTP 201 with user id'() {
        given:
        UserRegisterRequest request = USER_REGISTER_REQUEST
        mockedUserService.register(USER_REGISTER_REQUEST) >> USER_ID

        when:
        def response = restClient.with().body(request).when().post(USER_PATH + '/sign-up')

        then:
        response.statusCode() == 201
        and:
        response.headers().get("Location").value == USER_PATH + '/' + USER_ID
    }

    @Unroll
    def 'POST /api/users/sign-up should not register new user because of invalid user data: #userMessage'() {
        when:
        def response = restClient.with().body(request).when().post(USER_PATH + '/sign-up')

        then:
        response.statusCode() == 400

        where:
        request                                   || userMessage
        USER_REGISTER_REQUEST.withEmail(null)     || 'Email cannot be null.'
        USER_REGISTER_REQUEST.withEmail("")       || 'Email cannot be blank.'
        USER_REGISTER_REQUEST.withEmail("email")  || 'Not valid email regex.'
        USER_REGISTER_REQUEST.withEmail("em")     || 'Email too short.'
        USER_REGISTER_REQUEST.withPassword(null)     || 'Password cannot be null.'
        USER_REGISTER_REQUEST.withPassword("")       || 'Password cannot be blank.'
        USER_REGISTER_REQUEST.withPassword("em")     || 'Password too short.'
        USER_REGISTER_REQUEST.withFirstName(null)     || 'FirstName cannot be null.'
        USER_REGISTER_REQUEST.withFirstName("")       || 'FirstName cannot be blank.'
        USER_REGISTER_REQUEST.withLastName(null)     || 'LastName cannot be null.'
        USER_REGISTER_REQUEST.withLastName("")       || 'LastName cannot be blank.'
        USER_REGISTER_REQUEST.withUsername(null)     || 'Username cannot be null.'
        USER_REGISTER_REQUEST.withUsername("")       || 'Username cannot be blank.'
        USER_REGISTER_REQUEST.withUsername("em")     || 'Username too short.'
    }

    @TestConfiguration
    @ComponentScan
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        UserService userService() {
            return detachedMockFactory.Stub(UserService)
        }
    }
}
