package pl.fintech.dragons.dragonslending.sociallending.payment.account.domain;

import lombok.NonNull;
import lombok.Value;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

@Value
public class FrozenMoneyReleased implements DomainEvent {
    @NonNull UUID eventId = UUID.randomUUID();
    @NonNull Instant when;
    @NonNull UUID userId;
    @NonNull UUID accountNumber;

    public static FrozenMoneyReleased now(UUID userId, UUID accountNumber) {
        return new FrozenMoneyReleased(Instant.now(), userId, accountNumber);
    }

    public UUID getAggregateId() {
        return userId;
    }
}