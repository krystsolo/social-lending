package pl.fintech.dragons.dragonslending.security

import io.restassured.RestAssured
import io.restassured.specification.RequestSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import pl.fintech.dragons.dragonslending.identity.application.UserDetailsProvider
import pl.fintech.dragons.dragonslending.identity.domain.UserPrincipal
import spock.lang.Specification
import spock.mock.DetachedMockFactory

@SpringBootTest(
        classes = [
                SecurityConfig.class,
                StubConfig.class,
                DispatcherServletAutoConfiguration.class,
                ServletWebServerFactoryAutoConfiguration.class,
                WebEndpointAutoConfiguration.class,
                EndpointAutoConfiguration.class,
                SecurityAutoConfiguration.class
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("integration-test")
class LoginIT extends Specification {

    @LocalServerPort
    int serverPort

    @Autowired
    UserDetailsProvider userDetailsProvider

    @Autowired
    PasswordEncoder passwordEncoder

    RequestSpecification restClient

    def setup() {
        restClient = RestAssured.given()
                .port(serverPort)
                .accept(MediaType.APPLICATION_JSON.toString())
                .contentType(MediaType.APPLICATION_JSON.toString())
                .log().all()
    }

    def 'POST /api/login should log in when user credentials are correct'() {
        given:
        JwtAuthenticationFilter.UserCredentials credentials = new JwtAuthenticationFilter.UserCredentials("email@com", "password")
        userDetailsProvider.loadUserByUsername(credentials.getEmail()) >> new UserPrincipal(UUID.randomUUID(), credentials.getEmail(), passwordEncoder.encode("password"))

        when:
        def response = restClient.with().body(credentials).when().post('/api/login')

        then:
        response.statusCode() == 200
        and:
        response.headers().get(SecurityConstants.HEADER_STRING).value != null
    }

    def 'POST /api/login should not log in when user credentials are not correct'() {
        given:
        JwtAuthenticationFilter.UserCredentials credentials = new JwtAuthenticationFilter.UserCredentials("email@com", "not_correct_password")
        userDetailsProvider.loadUserByUsername(credentials.getEmail()) >> new UserPrincipal(UUID.randomUUID(), credentials.getEmail(), passwordEncoder.encode("password"))

        when:
        def response = restClient.with().body(credentials).when().post('/api/login')

        then:
        response.statusCode() == 401
    }

    @TestConfiguration
    @ComponentScan
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        UserDetailsProvider userDetailsProvider() {
            return detachedMockFactory.Stub(UserDetailsProvider)
        }
    }
}
