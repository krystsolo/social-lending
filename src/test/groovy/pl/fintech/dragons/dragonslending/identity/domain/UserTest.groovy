package pl.fintech.dragons.dragonslending.identity.domain

import pl.fintech.dragons.dragonslending.identity.UserFixture
import pl.fintech.dragons.dragonslending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.identity.domain.User
import spock.lang.Specification

import static pl.fintech.dragons.dragonslending.identity.UserFixture.USER

class UserTest extends Specification {

    def "should return proper dto"() {
        given:
        User user = UserFixture.USER

        when:
        UserDto userDto = user.toDto()

        then:
        user.email == userDto.email
        user.firstName == userDto.firstName
        user.lastName == userDto.lastName
        user.username == userDto.username
        user.id == userDto.id
    }
}
