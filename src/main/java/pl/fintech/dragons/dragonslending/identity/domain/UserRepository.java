package pl.fintech.dragons.dragonslending.identity.domain;

import pl.fintech.dragons.dragonslending.identity.application.UserCredentials;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    UserCredentials getCredentialsFor(String email);

    User save(User user);

    User getOne(UUID id);

    List<User> findAll();
}
