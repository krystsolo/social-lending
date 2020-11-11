package pl.fintech.dragons.dragonslending.offer.calculator;

import pl.fintech.dragons.dragonslending.offer.Offer;
import pl.fintech.dragons.dragonslending.offer.dto.CalculationDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OfferCalculator {

  private static final double FEE_RATE = 0.02;

  public CalculationDto calculate(Offer entity) {
    BigDecimal finalValue = entity.getLoanAmount()
        .add(entity.getLoanAmount()
            .multiply(BigDecimal.valueOf(entity.getInterestRate()/100 + FEE_RATE)));
    return CalculationDto.builder()
        .finalValue(finalValue.setScale(2, RoundingMode.HALF_UP))
        .periodValue(finalValue.divide(BigDecimal.valueOf(entity.getTimePeriod())).setScale(2, RoundingMode.HALF_UP))
        .build();
  }
}
