package pl.fintech.dragons.dragonslending.sociallending.auction.dto;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Value
@With
public class AuctionRequest {
  UUID id;

  @NotNull
  @DecimalMin(value = "0", message = "Loan amount value should not be less then 0")
  @DecimalMax(value = "10000", message = "Loan amount value should not be greater than 10000")
  BigDecimal loanAmount;

  @NotNull
  @Min(value = 1, message = "Time period value should not be less than 1")
  @Max(value = 36, message = "Time period value should not be greater than 36")
  Integer timePeriod;

  @NotNull
  @Min(value = 0, message = "Time period value should not be less than 1")
  @Max(value = 20, message = "Time period value should not be greater than 20")
  Float interestRate;

  @NotNull
  LocalDate endDate;
}
