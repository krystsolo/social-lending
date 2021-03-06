package pl.fintech.dragons.dragonslending.sociallending.payment.account.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    private BigDecimal balance;

    @Column(name = "frozen_amount")
    private BigDecimal frozenAmount;

    public Account(@NonNull UUID userId) {
        this.id = UUID.randomUUID();
        this.balance = BigDecimal.ZERO;
        this.frozenAmount = BigDecimal.ZERO;
        this.userId = userId;
    }

    public void withdraw(@NonNull BigDecimal amount) {
        validateAmountIsNotNegative(amount);
        if (availableBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Cannot withdraw more money which is now available");
        }

        this.balance = balance.subtract(amount);
    }

    public void deposit(@NonNull BigDecimal amount) {
        validateAmountIsNotNegative(amount);
        this.balance = balance.add(amount);
    }

    public BigDecimal availableBalance() {
        return balance.subtract(frozenAmount);
    }

    public void freeze(@NonNull BigDecimal amount) {
        if (availableBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Account balance cannot be negative");
        }
        this.frozenAmount = frozenAmount.add(amount);
    }

    public void unfreeze(@NonNull BigDecimal amount) {
        if (frozenAmount.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Cannot unfreeze more money then it was frozen on account");
        }
        this.frozenAmount = frozenAmount.subtract(amount);
    }

    public void unfreezeAllMoney() {
        this.frozenAmount = BigDecimal.ZERO;
    }

    private void validateAmountIsNotNegative(BigDecimal amount) {
        if (negativeAmount(amount)) {
            throw new IllegalArgumentException("Amount cannot be negative: " + amount);
        }
    }

    private boolean negativeAmount(BigDecimal amount) {
        return amount.signum() == -1;
    }
}
