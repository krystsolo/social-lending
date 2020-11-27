package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.Loan;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanRepository;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.AccountFinder;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountInfo;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Transactional
public class LoanRepaymentHandlerScheduler {

    private final LoanRepository loanRepository;
    private final AccountFinder accountFinder;
    private final EventPublisher eventPublisher;

    @Scheduled(cron = "0 0 1 * * *")
    public void handleRepaymentsOfLoanInstallments() {
        processRepaymentsForLoansWithUnpaidInstallments();
        processRepaymentsForLoansWithTodayAsNextInstallmentDate();
    }

    private void processRepaymentsForLoansWithUnpaidInstallments() {
        Set<Loan> loansWithUnpaidInstallments = loanRepository.findAllLoansWithUnpaidInstallments();
        for (Loan loan : loansWithUnpaidInstallments) {
            AccountInfo accountInfo = accountFinder.getAccountInfoFor(loan.getBorrowerId());
            List<DomainEvent> domainEvents = loan.payForUnpaidInstallments(accountInfo);
            eventPublisher.publish(domainEvents);
        }
    }

    private void processRepaymentsForLoansWithTodayAsNextInstallmentDate() {
        List<Loan> loansWithCurrentDayAsNextInstallmentDay = loanRepository.findAllLoansWithTodayAsNextInstallmentDay();
        for (Loan loan : loansWithCurrentDayAsNextInstallmentDay) {
            AccountInfo accountInfo = accountFinder.getAccountInfoFor(loan.getBorrowerId());
            List<DomainEvent> domainEvents = loan.payOffNextInstallment(accountInfo);
            eventPublisher.publish(domainEvents);
        }
    }
}

