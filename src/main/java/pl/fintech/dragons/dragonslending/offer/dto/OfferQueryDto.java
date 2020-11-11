package pl.fintech.dragons.dragonslending.offer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@Value
public class OfferQueryDto {
  UUID id;
  BigDecimal loanAmount;
  Integer timePeriod;
  Float interestRate;
  LocalDate endDate;
  CalculationDto calculation;
  UUID userId;
  String username;
}
