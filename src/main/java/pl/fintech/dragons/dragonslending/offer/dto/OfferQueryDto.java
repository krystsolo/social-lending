package pl.fintech.dragons.dragonslending.offer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import pl.fintech.dragons.dragonslending.offer.Offer;

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

  public static OfferQueryDto entityToDto(Offer entity, CalculationDto calculationDto, String username) {
    if (entity == null) {
      return null;
    }
    return OfferQueryDto.builder()
        .id(entity.getId())
        .loanAmount(entity.getLoanAmount())
        .timePeriod(entity.getTimePeriod())
        .interestRate(entity.getInterestRate())
        .endDate(entity.getEndDate())
        .calculation(calculationDto)
        .userId(entity.getId())
        .username(username)
        .build();
  }
}
