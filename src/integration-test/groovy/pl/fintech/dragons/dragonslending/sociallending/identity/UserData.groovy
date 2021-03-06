package pl.fintech.dragons.dragonslending.sociallending.identity

import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.web.UserRegisterRequest
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.User

class UserData {

    static final UUID USER_ID = UUID.randomUUID()

    static final User USER = new User(USER_ID, "email@com","username",
            "firstname", "lastname", "pwssword")

    static final UserRegisterRequest USER_REGISTER_REQUEST = new UserRegisterRequest(
            "email@com","username", "firstname", "lastname", "pwssword")

    static final UserDto USER_DTO = new UserDto(USER_ID, "email@com","username",
            "firstname", "lastname")
}
