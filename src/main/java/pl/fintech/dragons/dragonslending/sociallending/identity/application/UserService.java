package pl.fintech.dragons.dragonslending.sociallending.identity.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.web.UserRegisterRequest;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.User;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserFactory;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRegistered;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRepository;
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserFactory userFactory;
    private final UserRepository repository;
    private final AuthenticationFacade authenticationFacade;
    private final EventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public UserDto getUser(UUID id) {
        User user = repository.getOne(id);
        return user.toDto();
    }

    public UUID register(UserRegisterRequest userRegisterRequest) {
        User user = userFactory.from(userRegisterRequest);
        user = repository.save(user);
        UUID userId = user.getId();
        eventPublisher.publish(UserRegistered.now(userId));
        return userId;
    }

    public UserDto getCurrentLoggedUser() {
        UUID idOfCurrentLoggedUser = authenticationFacade.idOfCurrentLoggedUser();
        User user = repository.getOne(idOfCurrentLoggedUser);
        return user.toDto();
    }
}
