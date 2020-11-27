package pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loan_installment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class LoanInstallment {

    enum InstallmentStatus {
        PAID, UNPAID
    }

    @Id
    private UUID id;

    @Column(name = "calculated_lender_repayment_amount")
    private BigDecimal calculatedLenderRepaymentAmount;

    @Column(name = "lender_repayment_amount")
    private BigDecimal lenderRepaymentAmount;

    @Column(name = "calculated_system_repayment_amount")
    private BigDecimal calculatedSystemRepaymentAmount;

    @Column(name = "system_repayment_amount")
    private BigDecimal systemRepaymentAmount;

    @Column(name = "timely_repayment_time")
    private LocalDate timelyRepaymentTime;

    @Column(name = "repayment_time")
    private LocalDate repaymentTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InstallmentStatus status;

    LoanInstallment(BigDecimal calculatedLenderRepaymentAmount,
                    BigDecimal calculatedSystemRepaymentAmount) {
        this.id = UUID.randomUUID();
        this.calculatedLenderRepaymentAmount = calculatedLenderRepaymentAmount;
        this.lenderRepaymentAmount = BigDecimal.ZERO;
        this.calculatedSystemRepaymentAmount = calculatedSystemRepaymentAmount;
        this.systemRepaymentAmount = BigDecimal.ZERO;
        this.timelyRepaymentTime = LocalDate.now();
        this.repaymentTime = LocalDate.now();
        this.status = InstallmentStatus.UNPAID;
    }

    boolean isUnpaid() {
        return status == InstallmentStatus.UNPAID;
    }

    BigDecimal totalRepaymentAmount() {
        return systemRepaymentAmount.add(lenderRepaymentAmount);
    }

    Repayment payOff(@NonNull BigDecimal amount) {
        BigDecimal amountNeededToRepayLender = calculatedLenderRepaymentAmount.subtract(lenderRepaymentAmount);
        BigDecimal amountNeededToRepaySystem = calculatedSystemRepaymentAmount.subtract(systemRepaymentAmount);

        Repayment repayment = calculateRepaymentForInstallment(amount, amountNeededToRepayLender, amountNeededToRepaySystem);

        this.lenderRepaymentAmount = lenderRepaymentAmount.add(repayment.getLenderAmount());
        this.systemRepaymentAmount = systemRepaymentAmount.add(repayment.getSystemAmount());
        this.repaymentTime = LocalDate.now();

        if (lenderRepaymentAmount.equals(calculatedLenderRepaymentAmount) && systemRepaymentAmount.equals(calculatedSystemRepaymentAmount)) {
            this.status = InstallmentStatus.PAID;
        }

        return repayment;
    }

    private Repayment calculateRepaymentForInstallment(BigDecimal amount, BigDecimal amountNeededToRepayLender, BigDecimal amountNeededToRepaySystem) {
        BigDecimal amountLeft = amount;
        BigDecimal lenderAmount;
        BigDecimal systemAmount = BigDecimal.ZERO;

        if (amountLeft.compareTo(amountNeededToRepayLender) > 0) {
            lenderAmount = amountNeededToRepayLender;
            amountLeft = amountLeft.subtract(amountNeededToRepayLender);

            if (amountLeft.compareTo(amountNeededToRepaySystem) > 0) {
                systemAmount = amountNeededToRepaySystem;
            } else {
                systemAmount = amountLeft;
            }

        } else {
            lenderAmount = amountNeededToRepayLender;
        }

        return new Repayment(lenderAmount, systemAmount);
    }

}

