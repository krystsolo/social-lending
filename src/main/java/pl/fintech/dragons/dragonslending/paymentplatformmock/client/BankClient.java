package pl.fintech.dragons.dragonslending.paymentplatformmock.client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "bankapi-api", url = "${bankapi.url}")
public interface BankClient {

    @PostMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    void requestTransaction(@RequestBody BankTransaction bankTransaction);

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    class BankTransaction {
        private UUID sourceAccountNumber;
        private UUID targetAccountNumber;
        private BigDecimal amount;
    }
}
