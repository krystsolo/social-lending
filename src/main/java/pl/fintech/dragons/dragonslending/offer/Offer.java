package pl.fintech.dragons.dragonslending.offer;

import lombok.*;
import pl.fintech.dragons.dragonslending.identity.domain.User;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "offers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Offer {

  @Id
  private UUID id;

  @Column(name = "loan_amount")
  @NotNull
  @DecimalMin(value = "0", message = "Loan amount value should not be less then 0")
  @DecimalMax(value = "1000000", message = "Loan amount value should not be greater than 1000000")
  private BigDecimal loanAmount;

  @Column(name = "time_period")
  @NotNull
  @Min(value = 1, message = "Time period value should not be less than 1")
  @Max(value = 36, message = "Time period value should not be greater than 1000000")
  private Integer timePeriod;

  @Column(name = "interest_rate")
  @NotNull
  @Min(value = 0, message = "Time period value should not be less than 1")
  @Max(value = 20, message = "Time period value should not be greater than 1000000")
  private Float interestRate;

  @Column(name = "end_date")
  @NotNull
  private LocalDate endDate;

  @Column(name = "user_id")
  UUID userId;

  Offer(BigDecimal loanAmount, Integer timePeriod, Float interestRate, LocalDate endDate, UUID userId) {
    this.id = UUID.randomUUID();
    this.loanAmount = loanAmount;
    this.timePeriod = timePeriod;
    this.interestRate = interestRate;
    this.userId = userId;
    setEndDate(endDate);
  }

  void setEndDate(LocalDate endDate) {
    if(!this.endDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date must be in future");
    }
    this.endDate = endDate;
  }
}
