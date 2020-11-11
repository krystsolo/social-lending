package pl.fintech.dragons.dragonslending.offer.dto.mapper;

import pl.fintech.dragons.dragonslending.offer.Offer;
import pl.fintech.dragons.dragonslending.offer.dto.CalculationDto;
import pl.fintech.dragons.dragonslending.offer.dto.OfferQueryDto;

public class OfferMapper {
  public static OfferQueryDto getReturnDtoFromEntity(Offer entity, CalculationDto calculationDto, String username) {
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
