package pl.fintech.dragons.dragonslending.sociallending.offer.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Value
public class OfferQueryDto {
  UUID id;
  BigDecimal offerAmount;
  Float interestRate;
  Calculation calculation;
  UUID userId;
  String username;
  String auctionOwner;
  UUID auctionId;
}
