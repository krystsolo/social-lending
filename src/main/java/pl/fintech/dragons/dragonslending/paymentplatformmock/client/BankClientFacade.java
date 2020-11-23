package pl.fintech.dragons.dragonslending.paymentplatformmock.client;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class BankClientFacade {

    private final BankClient bankClient;

    public void requestMoneyTransfer(UUID sourceAccountNumber, UUID targetAccountNumber, BigDecimal amount) {
        bankClient.requestTransaction(new BankClient.BankTransaction(sourceAccountNumber, targetAccountNumber, amount));
    }
}
