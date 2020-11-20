package pl.fintech.dragons.dragonslending.sociallending.payment.account.domain;

import lombok.NonNull;
import lombok.Value;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;
import pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain.MoneyTransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface MoneyTransferEvent extends DomainEvent {

    MoneyTransactionType type();

    UUID getSourceAccountNumber();

    UUID getTargetAccountNumber();

    BigDecimal getAmount();


    @Value
    class MoneyWithdrawn implements MoneyTransferEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID sourceAccountNumber;
        @NonNull UUID targetAccountNumber;
        @NonNull BigDecimal amount;

        public static MoneyWithdrawn now(UUID sourceAccountNumber, UUID targetAccountNumber, BigDecimal amount) {
            return new MoneyWithdrawn(Instant.now(), sourceAccountNumber, targetAccountNumber, amount);
        }

        public MoneyTransactionType type() {
            return MoneyTransactionType.WITHDRAW;
        }

        public UUID getAggregateId() {
            return sourceAccountNumber;
        }
    }

    @Value
    class MoneyDeposited implements MoneyTransferEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID sourceAccountNumber;
        @NonNull UUID targetAccountNumber;
        @NonNull BigDecimal amount;

        public static MoneyDeposited now(UUID sourceAccountNumber, UUID targetAccountNumber, BigDecimal amount) {
            return new MoneyDeposited(Instant.now(), sourceAccountNumber, targetAccountNumber, amount);
        }

        public MoneyTransactionType type() {
            return MoneyTransactionType.DEPOSIT;
        }

        public UUID getAggregateId() {
            return targetAccountNumber;
        }
    }

    @Value
    class LendingMoneyTransferred implements MoneyTransferEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID sourceAccountNumber;
        @NonNull UUID targetAccountNumber;
        @NonNull BigDecimal amount;

        public static LendingMoneyTransferred now(UUID sourceAccountNumber, UUID targetAccountNumber, BigDecimal amount) {
            return new LendingMoneyTransferred(Instant.now(), sourceAccountNumber, targetAccountNumber, amount);
        }

        public MoneyTransactionType type() {
            return MoneyTransactionType.LENDING_TRANSFER;
        }

        public UUID getAggregateId() {
            return sourceAccountNumber;
        }
    }

    @Value
    class LoanInstallmentTransferred implements MoneyTransferEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID sourceAccountNumber;
        @NonNull UUID targetAccountNumber;
        @NonNull BigDecimal amount;

        public static LoanInstallmentTransferred now(UUID sourceAccountNumber, UUID targetAccountNumber, BigDecimal amount) {
            return new LoanInstallmentTransferred(Instant.now(), sourceAccountNumber, targetAccountNumber, amount);
        }

        public MoneyTransactionType type() {
            return MoneyTransactionType.LOAN_INSTALLMENT;
        }

        public UUID getAggregateId() {
            return targetAccountNumber;
        }
    }
}
