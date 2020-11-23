package pl.fintech.dragons.dragonslending.paymentplatformmock.client;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BankClientMock implements BankClient {

    @Override
    public void requestTransaction(BankTransaction bankTransaction) {
        log.debug("Requested transfer from Payment Mock Platform with data - from: {}, to: {}, amount: {}",
                bankTransaction.getSourceAccountNumber(), bankTransaction.getTargetAccountNumber(), bankTransaction.getAmount());
    }
}
