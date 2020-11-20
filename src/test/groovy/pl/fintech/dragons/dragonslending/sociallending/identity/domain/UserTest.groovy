package pl.fintech.dragons.dragonslending.sociallending.identity.domain

import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import spock.lang.Specification

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
