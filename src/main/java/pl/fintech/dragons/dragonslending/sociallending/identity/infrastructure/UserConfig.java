package pl.fintech.dragons.dragonslending.sociallending.identity.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.common.events.publisher.EventPublisherConfig;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDetailsProvider;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.User;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserFactory;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserRepository;
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationConfig;
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade;


@Configuration
@EnableJpaRepositories(basePackageClasses = UserRepositoryAdapter.class, considerNestedRepositories = true)
@EntityScan(basePackageClasses = User.class)
@Import({AuthenticationConfig.class, EventPublisherConfig.class})
public class UserConfig {

    @Bean
    UserRepository userRepository(UserRepositoryAdapter.UserJpaRepository jpaRepo) {
        return new UserRepositoryAdapter(jpaRepo);
    }

    @Bean
    UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                            AuthenticationFacade authenticationFacade, EventPublisher eventPublisher) {
        return new UserService(
                new UserFactory(passwordEncoder, userRepository),
                userRepository,
                authenticationFacade,
                eventPublisher);
    }

    @Bean
    UserDetailsProvider userDetailsProvider(UserRepository userRepository) {
        return new UserDetailsProvider(userRepository);
    }
}
