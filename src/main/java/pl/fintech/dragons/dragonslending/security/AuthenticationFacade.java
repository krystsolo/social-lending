package pl.fintech.dragons.dragonslending.security;

import java.util.UUID;

public interface AuthenticationFacade {
    UUID idOfCurrentLoggedUser();

    String emailOfCurrentLoggedUser();
}
