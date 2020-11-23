package pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.bankapi;

import lombok.extern.slf4j.Slf4j;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.BankApiService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
public class BankApiServiceMock  implements BankApiService {
    @Override
    public void requestWithdraw(UUID from, UUID to, BigDecimal amount) {
        log.debug("Requested withdraw from: {}, to: {}, amount: {}", from, to, amount);
    }
}
