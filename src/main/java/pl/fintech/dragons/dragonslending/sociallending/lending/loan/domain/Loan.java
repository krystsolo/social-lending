package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain;


import lombok.*;
import org.hibernate.annotations.BatchSize;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanInstallmentPaidOff.SystemFeeChargedOnLoan;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculation;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.AccountInfo;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanInstallmentPaidOff.*;

@Entity
@Table(name = "loan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id")
public class Loan {

    @Id
    private UUID id;

    @Column(name = "borrower_id")
    private UUID borrowerId;

    @Column(name = "lender_id")
    private UUID lenderId;

    @Column(name = "amount_to_repaid_to_lender")
    private BigDecimal amountToRepaidToLender;

    @Column(name = "system_fee")
    private BigDecimal systemFee;

    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @Column(name = "next_installment_date")
    private LocalDate nextInstallmentDate;

    @Column(name = "installments_number")
    private int installmentsNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "loan_id")
    @BatchSize(size = 40)
    private List<LoanInstallment> installments;

    public static Loan from(UUID borrowerId, UUID lenderId, LoanCalculation loanCalculation, int installmentsNumber) {
        return new Loan(
                UUID.randomUUID(),
                borrowerId,
                lenderId,
                loanCalculation.getAmountToRepaidToLender(),
                loanCalculation.getSystemFee(),
                LocalDateTime.now(),
                LocalDate.now().plusMonths(1),
                installmentsNumber,
                LoanStatus.ACTIVE,
                new ArrayList<>()
        );
    }

    public List<DomainEvent> payOffNextInstallment(@NonNull AccountInfo accountInfo) {
        LoanInstallment loanInstallment = new LoanInstallment(
                amountToRepaidToLender.divide(BigDecimal.valueOf(installmentsNumber), 2, RoundingMode.HALF_UP),
                systemFee.divide(BigDecimal.valueOf(installmentsNumber), 2, RoundingMode.HALF_UP));
        installments.add(loanInstallment);

        if (accountInfo.isBalanceEmpty()) {
            this.nextInstallmentDate = nextInstallmentDate.plusMonths(1);
            return Collections.singletonList(LoanInstallmentUnpaid.now(id, borrowerId, lenderId, calculatedInstallmentAmount()));
        }

        Repayment repayment = loanInstallment.payOff(accountInfo.getBalance());
        List<DomainEvent> domainEvents = loanEventsFrom(repayment);
        if (!repayment.totalAmount().equals(calculatedInstallmentAmount())) {
            domainEvents.add(LoanInstallmentUnpaid.now(id, borrowerId, lenderId, calculatedInstallmentAmount()));
        }

        if (isLoanPaidOff()) {
            LoanFinished loanFinished = finish(repayment);
            domainEvents.add(loanFinished);
        } else {
            this.nextInstallmentDate = nextInstallmentDate.plusMonths(1);
        }

        return domainEvents;
    }

    public List<DomainEvent> payForUnpaidInstallments(@NonNull AccountInfo accountInfo) {
        if (accountInfo.isBalanceEmpty()) {
            return Collections.emptyList();
        }

        List<LoanInstallment> unpaidInstallments = getUnpaidInstallments();
        Repayment repayment = processInstallmentRepayments(accountInfo, unpaidInstallments);

        List<DomainEvent> domainEvents = loanEventsFrom(repayment);

        if (isLoanPaidOff()) {
            LoanFinished loanFinished = finish(repayment);
            domainEvents.add(loanFinished);
        }

        return domainEvents;
    }

    public BigDecimal calculatedInstallmentAmount() {
        return amountToPaidOffByBorrower()
                .divide(BigDecimal.valueOf(installmentsNumber), 2, RoundingMode.HALF_UP);
    }

    private LoanFinished finish(Repayment repayment) {
        this.status = LoanStatus.FINISHED;
        return LoanFinished.now(id, borrowerId, lenderId, repayment.getLenderAmount());
    }

    private List<DomainEvent> loanEventsFrom(Repayment repayment) {
        List<DomainEvent> domainEvents = new ArrayList<>();
        domainEvents.add(LoanInstallmentPaidOffToLender.now(id, borrowerId, lenderId, repayment.getLenderAmount()));
        domainEvents.add(SystemFeeChargedOnLoan.now(id, borrowerId, lenderId, repayment.getSystemAmount()));
        return domainEvents;
    }

    private Repayment processInstallmentRepayments(AccountInfo accountInfo, List<LoanInstallment> unpaidInstallments) {
        BigDecimal availableBorrowerBalance = accountInfo.getBalance();
        List<Repayment> repayments = new ArrayList<>();

        for (LoanInstallment loanInstallment : unpaidInstallments) {
            Repayment repayment = loanInstallment.payOff(availableBorrowerBalance);
            repayments.add(repayment);
            availableBorrowerBalance = availableBorrowerBalance.subtract(repayment.totalAmount());

            if (availableBorrowerBalance.equals(BigDecimal.ZERO)) {
                break;
            }
        }

        return repayments.stream()
                .reduce(
                        new Repayment(BigDecimal.ZERO, BigDecimal.ZERO),
                        ((repayment1, repayment2) -> new Repayment(
                                repayment1.getLenderAmount().add(repayment2.getLenderAmount()),
                                repayment1.getSystemAmount().add(repayment2.getSystemAmount()))
                        )
                );
    }

    private List<LoanInstallment> getUnpaidInstallments() {
        return installments.stream()
                .filter(LoanInstallment::isUnpaid)
                .sorted(Comparator.comparing(LoanInstallment::getTimelyRepaymentTime))
                .collect(Collectors.toList());
    }
    private boolean isLoanPaidOff() {
        return alreadyPaidOffAmount().compareTo(amountToPaidOffByBorrower()) == 0;
    }

    private BigDecimal alreadyPaidOffAmount() {
        return installments.stream()
                .map(LoanInstallment::totalRepaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal amountToPaidOffByBorrower() {
        return amountToRepaidToLender.add(systemFee);
    }

    public List<LoanInstallmentQuery> getInstallments() {
        return installments.stream()
                .sorted(Comparator.comparing(LoanInstallment::getTimelyRepaymentTime))
                .map(installment ->
                        new LoanInstallmentQuery(
                                installment.totalRepaymentAmount(),
                                installment.getTimelyRepaymentTime(),
                                installment.getRepaymentTime(),
                                installment.getStatus().name()))
                .collect(Collectors.toList());
    }

    public boolean isActive() {
        return status == LoanStatus.ACTIVE;
    }
}
