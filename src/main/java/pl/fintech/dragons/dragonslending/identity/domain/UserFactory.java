package pl.fintech.dragons.dragonslending.identity.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.fintech.dragons.dragonslending.identity.application.web.UserRegisterRequest;

@RequiredArgsConstructor
@Slf4j
public class UserFactory {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public User from(UserRegisterRequest request) {
        validateUsernameNotExists(request.getUsername());
        validateEmailNotExists(request.getEmail());
        return new User(
                request.getEmail().toLowerCase(),
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                passwordEncoder.encode(request.getPassword()));
    }

    private void validateUsernameNotExists(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User already exists with such username");
        }
    }

    private void validateEmailNotExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists with such email");
        }
    }
}
