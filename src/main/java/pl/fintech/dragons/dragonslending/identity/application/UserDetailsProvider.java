package pl.fintech.dragons.dragonslending.identity.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.identity.domain.UserRepository;

@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserDetailsProvider implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return repository.getPrincipalFor(email);
    }
}
