package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain;

import lombok.NonNull;
import lombok.Value;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface LoanInstallmentPaidOff extends DomainEvent {

    UUID getBorrowerId();

    UUID getLenderId();

    BigDecimal getAmount();

    @Value
    class LoanInstallmentPaidOffToLender implements LoanInstallmentPaidOff {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID loanId;
        @NonNull UUID borrowerId;
        @NonNull UUID lenderId;
        @NonNull BigDecimal amount;

        public static LoanInstallmentPaidOffToLender now( UUID loanId, UUID borrowerId, UUID lenderId, BigDecimal amount) {
            return new LoanInstallmentPaidOffToLender(Instant.now(), loanId, borrowerId, lenderId, amount);
        }

        public UUID getAggregateId() {
            return loanId;
        }
    }

    @Value
    class LoanInstallmentUnpaid implements LoanInstallmentPaidOff {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID loanId;
        @NonNull UUID borrowerId;
        @NonNull UUID lenderId;
        @NonNull BigDecimal amount;

        public static LoanInstallmentUnpaid now( UUID loanId, UUID borrowerId, UUID lenderId, BigDecimal amount) {
            return new LoanInstallmentUnpaid(Instant.now(), loanId, borrowerId, lenderId, amount);
        }

        public UUID getAggregateId() {
            return loanId;
        }
    }

    @Value
    class SystemFeeChargedOnLoan implements LoanInstallmentPaidOff {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID loanId;
        @NonNull UUID borrowerId;
        @NonNull UUID lenderId;
        @NonNull BigDecimal amount;

        public static SystemFeeChargedOnLoan now(UUID loanId, UUID borrowerId, UUID lenderId, BigDecimal amount) {
            return new SystemFeeChargedOnLoan(Instant.now(), loanId, borrowerId, lenderId, amount);
        }

        public UUID getAggregateId() {
            return loanId;
        }
    }
}
