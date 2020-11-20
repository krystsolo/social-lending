package pl.fintech.dragons.dragonslending.sociallending.identity.domain;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = true)
public class UserPrincipal extends User {

    UUID id;

    public UserPrincipal(UUID id, String username, String password) {
        super(username, password, true, true, true, true, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        this.id = id;
    }

}
