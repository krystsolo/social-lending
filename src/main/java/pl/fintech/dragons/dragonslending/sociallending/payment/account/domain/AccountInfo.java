package pl.fintech.dragons.dragonslending.sociallending.payment.account.domain;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class AccountInfo {
    BigDecimal balance;
    BigDecimal availableFunds;

    public static AccountInfo from(Account account) {
        return new AccountInfo(account.getBalance(), account.availableBalance());
    }
}
