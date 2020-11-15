package pl.fintech.dragons.dragonslending.auction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@Value
public class AuctionRequest {
  UUID id;

  @NotNull
  @DecimalMin(value = "0", message = "Loan amount value should not be less then 0")
  @DecimalMax(value = "1000000", message = "Loan amount value should not be greater than 1000000")
  BigDecimal loanAmount;

  @NotNull
  @Min(value = 1, message = "Time period value should not be less than 1")
  @Max(value = 36, message = "Time period value should not be greater than 1000000")
  Integer timePeriod;

  @NotNull
  @Min(value = 0, message = "Time period value should not be less than 1")
  @Max(value = 20, message = "Time period value should not be greater than 1000000")
  Float interestRate;

  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.mm.yyyy")
  LocalDate endDate;
}
