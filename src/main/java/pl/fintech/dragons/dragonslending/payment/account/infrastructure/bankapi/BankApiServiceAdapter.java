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

    @Override
    public void requestWithdraw(UUID from, UUID to, BigDecimal amount) {
        bankApi.requestTransaction(new BankApi.BankTransaction(from, to, amount));
    }
}
