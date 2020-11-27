package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query;

import lombok.Value;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanInstallmentQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
class LoanDetailsView {

    enum LoanType {
        TAKEN, GRANTED
    }

    UUID id;
    String username;
    LoanType type;
    LocalDateTime creationTime;
    LocalDate nextInstallmentDate;
    BigDecimal calculatedRepaymentAmount;
    List<LoanInstallmentQuery> loanInstallments;
}

