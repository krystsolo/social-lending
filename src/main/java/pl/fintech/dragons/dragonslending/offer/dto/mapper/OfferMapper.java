package pl.fintech.dragons.dragonslending.offer.dto.mapper;

import pl.fintech.dragons.dragonslending.offer.Offer;
import pl.fintech.dragons.dragonslending.offer.dto.OfferDto;
import pl.fintech.dragons.dragonslending.offer.dto.OfferReturnDto;

public class OfferMapper {
  public static OfferDto getDtoFromEntity(Offer entity) {
    if (entity == null) {
      return null;
    }
    return OfferDto.builder()
        .id(entity.getId())
        .loanAmount(entity.getLoanAmount())
        .timePeriod(entity.getTimePeriod())
        .interestRate(entity.getInterestRate())
        .endDate(entity.getEndDate())
        .build();
  }

  public static OfferReturnDto getReturnDtoFromEntity(Offer entity, String user) {
    if (entity == null) {
      return null;
    }
    return OfferReturnDto.builder()
        .id(entity.getId())
        .loanAmount(entity.getLoanAmount())
        .timePeriod(entity.getTimePeriod())
        .interestRate(entity.getInterestRate())
        .endDate(entity.getEndDate())
        .build();
  }
}
