package pl.fintech.dragons.dragonslending.sociallending.payment.account.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.math.BigDecimal;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountInfo {
    BigDecimal balance;
    BigDecimal availableFunds;

    public static AccountInfo from(Account account) {
        return new AccountInfo(account.getBalance(), account.availableBalance());
    }

    public boolean isBalanceEmpty() {
        return balance.equals(BigDecimal.ZERO);
    }
}
