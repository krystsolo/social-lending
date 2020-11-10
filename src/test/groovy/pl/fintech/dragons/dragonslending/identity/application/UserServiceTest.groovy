package pl.fintech.dragons.dragonslending.identity.application

import pl.fintech.dragons.dragonslending.identity.application.web.UserRegisterRequest
import pl.fintech.dragons.dragonslending.identity.domain.User
import pl.fintech.dragons.dragonslending.identity.domain.UserFactory
import pl.fintech.dragons.dragonslending.identity.domain.UserRepository
import pl.fintech.dragons.dragonslending.security.AuthenticationFromSecurityContextRetriever
import spock.lang.Specification

import static pl.fintech.dragons.dragonslending.identity.UserFixture.*

class UserServiceTest extends Specification {
    UserFactory userFactory = Mock(UserFactory)
    UserRepository userRepository = Mock(UserRepository)
    AuthenticationFromSecurityContextRetriever authenticationFacade = Mock(AuthenticationFromSecurityContextRetriever)
    UserService userService = new UserService(userFactory, userRepository, authenticationFacade)

    def "should get userDto by id"() {
        given:
        thereIsUserWithId()

        when:
        def userDto = userService.getUser(USER_ID)

        then:
        userDto == USER.toDto()
    }

    def "should get userDto of current logged user"() {
        given:
        thereIsUserWithId()
        and:
        thereIsAuthenticationMocked()

        when:
        def userDto = userService.getCurrentLoggedUser()

        then:
        userDto == USER.toDto()
    }

    def "should register new user"() {
        given:
        mockDatabase()
        and:
        mockFactory()

        when:
        UUID id = userService.register(USER_REGISTER_REQUEST)

        then:
        id == USER_ID
    }

    void mockDatabase() {
        userRepository.save(_ as User) >> USER
    }

    void mockFactory() {
        userFactory.from(_ as UserRegisterRequest) >> USER
    }

    void thereIsUserWithId() {
        userRepository.getOne(USER_ID) >> USER
    }

    void thereIsAuthenticationMocked() {
        authenticationFacade.idOfCurrentLoggedUser() >> USER_ID
    }
}
