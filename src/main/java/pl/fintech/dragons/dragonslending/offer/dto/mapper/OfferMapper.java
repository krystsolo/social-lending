package pl.fintech.dragons.dragonslending.offer.dto.mapper;

import pl.fintech.dragons.dragonslending.offer.Offer;
import pl.fintech.dragons.dragonslending.offer.calculator.OfferCalculator;
import pl.fintech.dragons.dragonslending.offer.dto.OfferQueryDto;

public class OfferMapper {
  public OfferQueryDto getReturnDtoFromEntity(Offer entity, String user) {
    if (entity == null) {
      return null;
    }
    return OfferQueryDto.builder()
        .id(entity.getId())
        .loanAmount(entity.getLoanAmount())
        .timePeriod(entity.getTimePeriod())
        .interestRate(entity.getInterestRate())
        .endDate(entity.getEndDate())
        .calculation(OfferCalculator.calculate(entity.getLoanAmount(), entity.getInterestRate(), entity.getTimePeriod()))
        .user(user)
        .build();
  }
}
