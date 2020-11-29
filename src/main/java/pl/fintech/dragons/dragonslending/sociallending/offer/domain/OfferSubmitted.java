package pl.fintech.dragons.dragonslending.sociallending.offer.domain;

import lombok.NonNull;
import lombok.Value;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
public class OfferSubmitted implements DomainEvent {

    @NonNull UUID eventId = UUID.randomUUID();
    @NonNull Instant when;
    @NonNull UUID auctionId;
    @NonNull UUID offerId;
    @NonNull UUID userId;
    @NonNull BigDecimal amount;

    public static OfferSubmitted now(UUID auctionId, UUID offerId, UUID userId, BigDecimal amount) {
        return new OfferSubmitted(Instant.now(), auctionId, offerId, userId, amount);
    }

    public UUID getAggregateId() {
        return offerId;
    }
}