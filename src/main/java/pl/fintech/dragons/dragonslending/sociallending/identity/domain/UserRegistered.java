package pl.fintech.dragons.dragonslending.sociallending.identity.domain;

import lombok.NonNull;
import lombok.Value;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

@Value
public class UserRegistered implements DomainEvent {

    @NonNull UUID eventId = UUID.randomUUID();
    @NonNull Instant when;
    @NonNull UUID userId;

    public static UserRegistered now(UUID userId) {
        return new UserRegistered(Instant.now(), userId);
    }

    public UUID getAggregateId() {
        return userId;
    }
}
