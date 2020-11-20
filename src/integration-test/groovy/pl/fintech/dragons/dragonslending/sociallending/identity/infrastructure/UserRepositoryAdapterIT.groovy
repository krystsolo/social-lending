package pl.fintech.dragons.dragonslending.sociallending.identity.infrastructure

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.fintech.dragons.dragonslending.PostgreSQLContainerSpecification
import pl.fintech.dragons.dragonslending.sociallending.identity.UserData
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.User
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserPrincipal
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRepository
import spock.lang.Subject

@DataJpaTest
@ActiveProfiles("integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = UserConfig.class)
class UserRepositoryAdapterIT extends PostgreSQLContainerSpecification {

    @Subject
    @Autowired
    UserRepository userRepository

    def "should save user"() {
        given:
        User user = UserData.USER

        when:
        userRepository.save(user)

        then:
        def fromDb = userRepository.findAll()
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == user.id
            firstName == user.firstName
            lastName == user.lastName
            username == user.username
            email == user.email
            password == user.password
        }
    }

    def "should retrieve user"() {
        given:
        User user = UserData.USER
        userRepository.save(user)

        when:
        def fromDb = userRepository.getOne(user.id)

        then:
        with(fromDb) {
            id == user.id
            firstName == user.firstName
            lastName == user.lastName
            username == user.username
            email == user.email
            password == user.password
        }
    }

    def "should find user by username ignoring case"() {
        given:
        User user = UserData.USER
        userRepository.save(user)

        when:
        def fromDb = userRepository.findByUsername(user.username.toUpperCase())

        then:
        fromDb.isPresent()

        and:
        with(fromDb.get()) {
            id == user.id
            firstName == user.firstName
            lastName == user.lastName
            username == user.username
            email == user.email
            password == user.password
        }
    }

    def "should find user by email ignoring case"() {
        given:
        User user = UserData.USER
        userRepository.save(user)

        when:
        def fromDb = userRepository.findByEmail(user.email.toUpperCase())

        then:
        fromDb.isPresent()

        and:
        with(fromDb.get()) {
            id == user.id
            firstName == user.firstName
            lastName == user.lastName
            username == user.username
            email == user.email
            password == user.password
        }
    }

    def "should retrieve user credentials by user email"() {
        given:
        User user = UserData.USER
        userRepository.save(user)

        when:
        UserPrincipal userPrincipal = userRepository.getPrincipalFor(user.email)

        then:
        with(userPrincipal) {
            getId() == user.id
            getUsername() == user.email
            getPassword() == user.password
        }
    }
}
