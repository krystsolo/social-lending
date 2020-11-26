package pl.fintech.dragons.dragonslending.sociallending.auction;

import lombok.NonNull;
import lombok.Value;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

@Value
public class AuctionTerminated implements DomainEvent{

  @NonNull UUID eventId = UUID.randomUUID();
  @NonNull Instant when;
  @NonNull UUID userId;
  @NonNull UUID auctionId;

  public static AuctionTerminated now(UUID userId, UUID auctionId) {
    return new AuctionTerminated(Instant.now(), userId, auctionId);
  }

  public UUID getAggregateId() {
    return userId;
  }
}
