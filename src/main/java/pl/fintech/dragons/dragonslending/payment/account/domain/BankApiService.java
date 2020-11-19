package pl.fintech.dragons.dragonslending.payment.account.domain;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankApiService {
    void requestWithdraw(UUID from, UUID to, BigDecimal amount);
}
