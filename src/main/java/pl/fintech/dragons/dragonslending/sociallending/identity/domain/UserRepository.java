package pl.fintech.dragons.dragonslending.sociallending.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    UserPrincipal getPrincipalFor(String email);

    User save(User user);

    User getOne(UUID id);

    List<User> findAll();
}
