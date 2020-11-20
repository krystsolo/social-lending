package pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.bankapi;

import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "bankapi-api", url = "${bankapi.url}")
public interface BankApi {

    @GetMapping(value = "/accounts/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    Account getAccount(@PathVariable UUID accountNumber);

    @PostMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> createAccount(@RequestBody UserId userId);

    @PostMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    void requestTransaction(@RequestBody BankTransaction bankTransaction);

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    class Account {
        private UUID number;
        private String name;
        private BigDecimal accountBalance;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    class UserId {
        private String name;
    }

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

