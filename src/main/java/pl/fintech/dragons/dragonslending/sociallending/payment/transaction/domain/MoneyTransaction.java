package pl.fintech.dragons.dragonslending.sociallending.payment.transaction.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "money_transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class MoneyTransaction {

    @Id
    private UUID id;

    @Column(name = "source_account_number")
    private UUID sourceAccountNumber;

    @Column(name = "target_account_number")
    private UUID targetAccountNumber;

    @Enumerated(EnumType.STRING)
    private MoneyTransactionType type;

    private BigDecimal amount;

    private LocalDateTime time;

    public MoneyTransaction(@NonNull UUID sourceAccountNumber, @NonNull UUID targetAccountNumber, @NonNull MoneyTransactionType type, @NonNull BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.sourceAccountNumber = sourceAccountNumber;
        this.targetAccountNumber = targetAccountNumber;
        this.type = type;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }
}
