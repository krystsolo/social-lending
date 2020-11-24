package pl.fintech.dragons.dragonslending.sociallending.offer.dto;

import lombok.Builder;
import lombok.Value;
import pl.fintech.dragons.dragonslending.sociallending.loanCalculator.LoanCalculationDto;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Value
public class OfferQueryDto {
  UUID id;
  BigDecimal offerAmount;
  Float interestRate;
  LoanCalculationDto calculation;
  UUID userId;
  String username;
}
