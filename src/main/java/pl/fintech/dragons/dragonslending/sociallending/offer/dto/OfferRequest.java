package pl.fintech.dragons.dragonslending.sociallending.offer.dto;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Value
@With
public class OfferRequest {
  @NotNull
  @DecimalMin(value = "0", message = "Offer amount should not be less than 0")
  @DecimalMax(value = "10000", message = "Offer amount should not be greater than 10000")
  BigDecimal offerAmount;

  @NotNull
  @Min(value = 0, message = "Interest rate should not be less than 0")
  @Max(value = 20, message = "Interest rate should not be greater than 20")
  Float interestRate;

  @NotNull
  UUID auctionId;
}
