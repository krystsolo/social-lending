package pl.fintech.dragons.dragonslending.payment.account.infrastructure.bankapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import pl.fintech.dragons.dragonslending.common.errors.InternalServerErrorException;
import pl.fintech.dragons.dragonslending.payment.account.domain.BankApiService;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
public class BankApiServiceAdapter implements BankApiService {

    private final BankApi bankApi;

    public BigDecimal getAccountBalanceFor(UUID accountNumber) {
        return bankApi.getAccount(accountNumber).getAccountBalance();
    }

    public UUID createAccountFor(String userId) {
        ResponseEntity<Void> account = bankApi.createAccount(new BankApi.UserId(userId));
        URI location = account.getHeaders().getLocation();
        if (location == null) {
            throw new InternalServerErrorException("Could not retrieve accountId during account creation process");
        }
        String locationUrl = location.getRawPath();
        String id = locationUrl.substring(locationUrl.lastIndexOf("/") + 1);
        return UUID.fromString(id);
    }

    public void requestTransaction(UUID from, UUID to, BigDecimal amount) {
        bankApi.requestTransaction(new BankApi.BankTransaction(from, to, amount));
    }

    @Override
    public void requestWithdraw(UUID to, BigDecimal amount) {

    }
}
