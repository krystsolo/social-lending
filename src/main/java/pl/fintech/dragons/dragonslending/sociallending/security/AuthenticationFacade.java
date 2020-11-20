package pl.fintech.dragons.dragonslending.sociallending.security;

import java.util.UUID;

public interface AuthenticationFacade {
    UUID idOfCurrentLoggedUser();

    String emailOfCurrentLoggedUser();
}
