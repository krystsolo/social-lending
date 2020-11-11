package pl.fintech.dragons.dragonslending.offer.calculator;

import pl.fintech.dragons.dragonslending.offer.dto.CalculationDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OfferCalculator {
  public CalculationDto calculate(BigDecimal initialValue, Float interestRate, Integer timePeriod) {
    BigDecimal finalValue = initialValue.add(initialValue.multiply(BigDecimal.valueOf(interestRate)));
    return CalculationDto.builder()
        .finalValue(finalValue.setScale(2, RoundingMode.HALF_UP))
        .periodValue(finalValue.divide(BigDecimal.valueOf(timePeriod)).setScale(2, RoundingMode.HALF_UP))
        .build();
  }
}
