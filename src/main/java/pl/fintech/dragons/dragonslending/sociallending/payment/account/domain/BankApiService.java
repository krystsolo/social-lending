package pl.fintech.dragons.dragonslending.sociallending.payment.account.domain;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankApiService {
    void requestWithdraw(UUID from, UUID to, BigDecimal amount);
}
