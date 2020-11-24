package pl.fintech.dragons.dragonslending.sociallending.auction.dto;

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
  UUID userId;
  String username;
}
