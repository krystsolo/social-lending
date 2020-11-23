package pl.fintech.dragons.dragonslending.paymentplatformmock;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(PaymentController.PAYMENT_PATH)
@RequiredArgsConstructor
@Slf4j
class PaymentController {

    static final String PAYMENT_PATH = "/api/paymentplatform";
    private final PaymentService paymentService;

    @PostMapping("/deposit")
    void registerDeposit(@RequestBody @Valid DepositRequest depositRequest) {
        log.debug("REST request to register new deposit : {}", depositRequest);
        paymentService.registerDeposit(depositRequest);
    }
}

@Value
class DepositRequest {
    @NonNull
    UUID fromAccountNumber;
    @NonNull
    @DecimalMin("1")
    BigDecimal amount;
}