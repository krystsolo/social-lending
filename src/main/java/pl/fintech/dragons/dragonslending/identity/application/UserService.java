package pl.fintech.dragons.dragonslending.identity.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.identity.application.web.UserRegisterRequest;
import pl.fintech.dragons.dragonslending.identity.domain.User;
import pl.fintech.dragons.dragonslending.identity.domain.UserFactory;
import pl.fintech.dragons.dragonslending.identity.domain.UserRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserFactory userFactory;
    private final UserRepository repository;

    @Transactional(readOnly = true)
    public UserDto getUser(UUID id) {
        User user = repository.getOne(id);
        return user.toDto();
    }

    public UUID register(UserRegisterRequest userRegisterRequest) {
        User user = userFactory.from(userRegisterRequest);
        user = repository.save(user);
        return user.getId();
    }
}
