package pl.fintech.dragons.dragonslending.sociallending.offer;

import lombok.NonNull;
import lombok.Value;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
public class OfferSelected implements DomainEvent {

    @NonNull UUID eventId = UUID.randomUUID();
    @NonNull Instant when;
    @NonNull UUID auctionId;
    @NonNull UUID offerId;
    @NonNull UUID borrowerId;
    @NonNull UUID lenderId;
    @NonNull BigDecimal amount;
    @NonNull Float interestRate;
    @NonNull Integer timePeriod;

    public static OfferSelected now(UUID auctionId, UUID offerId, UUID borrowerId, UUID lenderId, BigDecimal amount, Float interestRate, Integer timePeriod) {
        return new OfferSelected(Instant.now(), auctionId, offerId, borrowerId, lenderId, amount, interestRate, timePeriod);
    }

    public UUID getAggregateId() {
        return offerId;
    }
}