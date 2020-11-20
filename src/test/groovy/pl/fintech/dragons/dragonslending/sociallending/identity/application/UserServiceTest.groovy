package pl.fintech.dragons.dragonslending.sociallending.identity.application

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.sociallending.identity.application.web.UserRegisterRequest
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.User
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserFactory
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRegistered
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRepository
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFromSecurityContextRetriever
import spock.lang.Specification

import static pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture.*

class UserServiceTest extends Specification {
    UserFactory userFactory = Mock(UserFactory)
    UserRepository userRepository = Mock(UserRepository)
    AuthenticationFromSecurityContextRetriever authenticationFacade = Mock(AuthenticationFromSecurityContextRetriever)
    EventPublisher eventPublisher = Mock(EventPublisher)
    UserService userService = new UserService(userFactory, userRepository, authenticationFacade, eventPublisher)

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
        1 * eventPublisher.publish(_ as UserRegistered)
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
