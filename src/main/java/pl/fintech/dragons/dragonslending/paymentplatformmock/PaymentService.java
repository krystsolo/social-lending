package pl.fintech.dragons.dragonslending.paymentplatformmock;

import lombok.RequiredArgsConstructor;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.paymentplatformmock.client.BankClientFacade;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.AccountFinder;
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade;

import java.util.UUID;

@RequiredArgsConstructor
class PaymentService {

    private final BankClientFacade bankClientFacade;
    private final EventPublisher eventPublisher;
    private final AccountFinder accountFinder;
    private final AuthenticationFacade authenticationFacade;

    void registerDeposit(DepositRequest depositRequest) {
        UUID systemAccount = accountFinder.getSystemAccount().number();
        UUID userId = authenticationFacade.idOfCurrentLoggedUser();
        bankClientFacade.requestMoneyTransfer(
                        depositRequest.getFromAccountNumber(),
                        systemAccount,
                        depositRequest.getAmount());
        eventPublisher.publish(
                MoneyDepositedFromExternalSource.now(
                        userId,
                        depositRequest.getFromAccountNumber(),
                        systemAccount,
                        depositRequest.getAmount()));
    }
}
