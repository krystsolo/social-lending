package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain;

import lombok.NonNull;
import lombok.Value;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
public class LoanFinished implements DomainEvent {

    @NonNull UUID eventId = UUID.randomUUID();
    @NonNull Instant when;
    @NonNull UUID loanId;
    @NonNull UUID borrowerId;
    @NonNull UUID lenderId;
    @NonNull BigDecimal amountBorrowed;

    public static LoanFinished now(UUID loanId, UUID borrowerId, UUID lenderId, BigDecimal amountBorrowed) {
        return new LoanFinished(Instant.now(), loanId, borrowerId, lenderId, amountBorrowed);
    }

    public UUID getAggregateId() {
        return loanId;
    }

}
