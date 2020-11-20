package pl.fintech.dragons.dragonslending.sociallending.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserPrincipal;

import java.util.UUID;

public class AuthenticationFromSecurityContextRetriever implements AuthenticationFacade {

    @Override
    public UUID idOfCurrentLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        return userPrincipal.getId();
    }

    @Override
    public String emailOfCurrentLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        return userPrincipal.getUsername();
    }
}
