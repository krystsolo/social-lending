package pl.fintech.dragons.dragonslending.identity.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.fintech.dragons.dragonslending.identity.application.UserDetailsProvider;
import pl.fintech.dragons.dragonslending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.identity.domain.User;
import pl.fintech.dragons.dragonslending.identity.domain.UserFactory;
import pl.fintech.dragons.dragonslending.identity.domain.UserRepository;
import pl.fintech.dragons.dragonslending.security.AuthenticationConfig;
import pl.fintech.dragons.dragonslending.security.AuthenticationFacade;


@Configuration
@EnableJpaRepositories(basePackageClasses = UserRepositoryAdapter.class, considerNestedRepositories = true)
@EntityScan(basePackageClasses = User.class)
@Import(AuthenticationConfig.class)
public class UserConfig {

    @Bean
    UserRepository userRepository(UserRepositoryAdapter.UserJpaRepository jpaRepo) {
        return new UserRepositoryAdapter(jpaRepo);
    }

    @Bean
    UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationFacade authenticationFacade) {
        return new UserService(new UserFactory(passwordEncoder, userRepository), userRepository, authenticationFacade);
    }

    @Bean
    UserDetailsProvider userDetailsProvider(UserRepository userRepository) {
        return new UserDetailsProvider(userRepository);
    }
}
