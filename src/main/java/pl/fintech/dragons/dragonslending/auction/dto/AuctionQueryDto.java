package pl.fintech.dragons.dragonslending.auction.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Value
public class AuctionQueryDto {
  UUID id;
  BigDecimal loanAmount;
  Integer timePeriod;
  Float interestRate;
  LocalDate endDate;
  CalculationDto calculation;
  UUID userId;
  String username;
}
