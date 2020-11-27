package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
class LoanQuery {
    UUID id;
    String username;
    BigDecimal amount;
    LocalDateTime creationTime;
    LocalDate nextInstallmentDate;
    int installmentsNumber;
}
