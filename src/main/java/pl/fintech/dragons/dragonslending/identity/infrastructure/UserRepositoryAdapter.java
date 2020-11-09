package pl.fintech.dragons.dragonslending.identity.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.fintech.dragons.dragonslending.identity.application.UserCredentials;
import pl.fintech.dragons.dragonslending.identity.domain.User;
import pl.fintech.dragons.dragonslending.identity.domain.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository repository;

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public User getOne(UUID id) {
        return repository.getOne(id);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsernameContainingIgnoreCase(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmailContainingIgnoreCase(email);
    }

    @Override
    public UserCredentials getCredentialsFor(String email) {
        return repository.getCredentialsByEmailContainingIgnoreCase(email);
    }

    interface UserJpaRepository extends JpaRepository<User, UUID> {
        Optional<User> findByUsernameContainingIgnoreCase(String username);
        Optional<User> findByEmailContainingIgnoreCase(String email);
        UserCredentials getCredentialsByEmailContainingIgnoreCase(String email);
    }
}
