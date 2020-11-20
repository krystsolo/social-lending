package pl.fintech.dragons.dragonslending.sociallending.identity.application.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fintech.dragons.dragonslending.common.HeaderUtil;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static pl.fintech.dragons.dragonslending.sociallending.identity.application.web.UserController.USER_PATH;

@RestController
@RequestMapping(USER_PATH)
@RequiredArgsConstructor
@Slf4j
class UserController {

    static final String USER_PATH = "/api/users";
    private final UserService userService;

    @Value("${pl.fintech.app-name}")
    private String applicationName;
    private static final String ENTITY_NAME = "user";

    @GetMapping("/{id}")
    UserDto getUser(@PathVariable UUID id) {
        log.debug("REST request to get User : {}", id);
        return userService.getUser(id);
    }

    @GetMapping("/self")
    UserDto getCurrentLoggedUser() {
        log.debug("REST request to get current logged User");
        return userService.getCurrentLoggedUser();
    }

    @PostMapping("/sign-up")
    ResponseEntity<Void> registerUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest) throws URISyntaxException {
        log.debug("REST request to register new User : {}", userRegisterRequest);
        UUID userId = userService.register(userRegisterRequest);
        return ResponseEntity.created(new URI(USER_PATH + "/" + userId))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userId.toString()))
                .build();
    }
}