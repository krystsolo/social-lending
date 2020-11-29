package pl.fintech.dragons.dragonslending.sociallending.payment.account.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanInstallmentPaidOff;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Transactional
public class LoanRepaymentEventHandler {

    private final AccountRepository accountRepository;
    private final EventPublisher eventPublisher;

    @EventListener
    public void handleLoanInstallmentPaidOff(LoanInstallmentPaidOff.LoanInstallmentPaidOffToLender event) {
        BigDecimal amount = event.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) < 1) {
            return;
        }

        Account borrowerAccount = accountRepository.getOneByUserId(event.getBorrowerId());
        checkIfBorrowerAvailableBalanceIsEnoughToBeWithdrawn(borrowerAccount, amount);
        borrowerAccount.withdraw(amount);

        Account lenderAccount = accountRepository.getOneByUserId(event.getLenderId());
        lenderAccount.deposit(amount);

        eventPublisher.publish(
                MoneyTransferEvent.LoanInstallmentTransferred.now(
                        event.getBorrowerId(),
                        event.getLenderId(),
                        amount));
    }

    @EventListener
    public void handleSystemFeePaidOff(LoanInstallmentPaidOff.SystemFeeChargedOnLoan event) {
        BigDecimal amount = event.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) < 1) {
            return;
        }

        Account borrowerAccount = accountRepository.getOneByUserId(event.getBorrowerId());
        checkIfBorrowerAvailableBalanceIsEnoughToBeWithdrawn(borrowerAccount, amount);
        borrowerAccount.withdraw(amount);

        SystemAccountNumber systemAccountNumber = accountRepository.getSystemAccountNumber();
        Account systemAccount = accountRepository.getOne(systemAccountNumber.number());
        systemAccount.deposit(amount);

        eventPublisher.publish(
                MoneyTransferEvent.LoanInstallmentTransferred.now(
                        event.getBorrowerId(),
                        systemAccountNumber.number(),
                        amount));
    }

    private void checkIfBorrowerAvailableBalanceIsEnoughToBeWithdrawn(Account borrowerAccount, BigDecimal amount) {
        if (borrowerAccount.availableBalance().compareTo(amount) < 0) {
            borrowerAccount.unfreezeAllMoney();
            eventPublisher.publish(
                    FrozenMoneyReleased.now(
                            borrowerAccount.getUserId(),
                            borrowerAccount.getId()));
        }
    }
}
