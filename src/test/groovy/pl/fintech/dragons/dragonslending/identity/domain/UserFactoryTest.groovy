package pl.fintech.dragons.dragonslending.identity.domain

import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

import static pl.fintech.dragons.dragonslending.identity.UserFixture.USER_REGISTER_REQUEST

class UserFactoryTest extends Specification {
    PasswordEncoder passwordEncoder = Mock(PasswordEncoder)
    UserRepository userRepository = Mock(UserRepository)
    UserFactory userFactory = new UserFactory(passwordEncoder, userRepository)

    def "should create proper user"() {
        given:
        passwordEncoder.encode(_) >> "passwordHashed"
        userRepository.findByUsername(_ as String) >> Optional.empty()
        userRepository.findByEmail(_ as String) >> Optional.empty()

        when:
        User user = userFactory.from(USER_REGISTER_REQUEST)

        then:
        user.id != null
        user.username == USER_REGISTER_REQUEST.username
        user.lastName == USER_REGISTER_REQUEST.lastName
        user.firstName == USER_REGISTER_REQUEST.firstName
        user.email == USER_REGISTER_REQUEST.email
        user.password == "passwordHashed"
    }

    def "should throw exception when username already exists"() {
        given:
        passwordEncoder.encode(_) >> "passwordHashed"
        userRepository.findByUsername(_ as String) >> Optional.of(new User())
        userRepository.findByEmail(_ as String) >> Optional.empty()

        when:
        userFactory.from(USER_REGISTER_REQUEST)

        then:
        thrown(IllegalArgumentException)
    }

    def "should throw exception when email already exists"() {
        given:
        passwordEncoder.encode(_) >> "passwordHashed"
        userRepository.findByEmail(_ as String) >> Optional.of(new User())
        userRepository.findByUsername(_ as String) >> Optional.empty()

        when:
        userFactory.from(USER_REGISTER_REQUEST)

        then:
        thrown(IllegalArgumentException)
    }
}
