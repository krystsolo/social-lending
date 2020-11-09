package pl.fintech.dragons.dragonslending.identity.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.fintech.dragons.dragonslending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.identity.domain.User;
import pl.fintech.dragons.dragonslending.identity.domain.UserFactory;
import pl.fintech.dragons.dragonslending.identity.domain.UserRepository;

@Configuration
@EnableJpaRepositories(basePackageClasses = UserRepositoryAdapter.class, considerNestedRepositories = true)
@EntityScan(basePackageClasses = User.class)
public class UserConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserRepository userRepository(UserRepositoryAdapter.UserJpaRepository jpaRepo) {
        return new UserRepositoryAdapter(jpaRepo);
    }

    @Bean
    UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserService(new UserFactory(passwordEncoder, userRepository), userRepository);
    }
}
