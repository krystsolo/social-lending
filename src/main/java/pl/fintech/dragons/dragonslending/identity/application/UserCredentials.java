package pl.fintech.dragons.dragonslending.identity.application;

import java.util.UUID;

public interface UserCredentials {
    String getEmail();
    String getPassword();
    UUID getId();

    default boolean passwordMatches(String password) {
        return getPassword().equals(password);
    }
}
