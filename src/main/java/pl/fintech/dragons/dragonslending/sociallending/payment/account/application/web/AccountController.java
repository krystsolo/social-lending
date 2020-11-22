package pl.fintech.dragons.dragonslending.sociallending.payment.account.application.web;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.AccountFinder;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.WithdrawMoneyCommandHandler;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountInfo;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/account/")
@RequiredArgsConstructor
@Slf4j
class AccountController {

    private final AccountFinder accountFinder;
    private final WithdrawMoneyCommandHandler withdrawMoneyCommandHandler;

    @GetMapping("/self")
    AccountInfo getCurrentLoggedUserAccount() {
        return accountFinder.getAccountInfoOfCurrentLoggedUser();
    }

    @PostMapping("/withdraw")
    void withdraw(@RequestBody @Valid WithdrawRequest withdrawRequest) {
        log.debug("REST request to withdraw money from system account: {}", withdrawRequest);
        withdrawMoneyCommandHandler.withdraw(withdrawRequest.getRequestedAccountNumber(), withdrawRequest.getAmount());
    }
}

@Value
@With
class WithdrawRequest {

    @NotNull
    UUID requestedAccountNumber;

    @NotNull
    @DecimalMin("1")
    BigDecimal amount;
}