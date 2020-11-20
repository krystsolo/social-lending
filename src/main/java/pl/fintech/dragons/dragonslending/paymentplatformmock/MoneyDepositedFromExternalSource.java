package pl.fintech.dragons.dragonslending.paymentplatformmock;

import lombok.NonNull;
import lombok.Value;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
public class MoneyDepositedFromExternalSource implements DomainEvent {

    @NonNull UUID eventId = UUID.randomUUID();
    @NonNull Instant when;
    @NonNull UUID userId;
    @NonNull UUID sourceAccountNumber;
    @NonNull UUID targetAccountNumber;
    @NonNull BigDecimal amount;

    public static MoneyDepositedFromExternalSource now(UUID userId, UUID sourceAccountNumber, UUID targetAccountNumber, BigDecimal amount) {
        return new MoneyDepositedFromExternalSource(Instant.now(), userId, sourceAccountNumber, targetAccountNumber, amount);
    }

    public UUID getAggregateId() {
        return userId;
    }
}
