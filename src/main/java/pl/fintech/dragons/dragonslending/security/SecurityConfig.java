package pl.fintech.dragons.dragonslending.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.fintech.dragons.dragonslending.identity.application.UserDetailsProvider;

import static pl.fintech.dragons.dragonslending.security.SecurityConstants.SIGN_IN_URL;
import static pl.fintech.dragons.dragonslending.security.SecurityConstants.SIGN_UP_URL;

@Configuration
@RequiredArgsConstructor
@Import(AuthenticationConfig.class)
class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsProvider userDetailsProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsProvider).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .addFilter(getJwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userDetailsProvider))
                .logout()
                .logoutUrl("/api/logout")
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    public JwtAuthenticationFilter getJwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authenticationManager);
        filter.setFilterProcessesUrl(SIGN_IN_URL);
        return filter;
    }
}
