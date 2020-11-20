package pl.fintech.dragons.dragonslending.sociallending.payment.account.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
@Getter(AccessLevel.NONE)
public class SystemAccountNumber {
    UUID accountNumber;

    public UUID number() {
        return accountNumber;
    }
}
