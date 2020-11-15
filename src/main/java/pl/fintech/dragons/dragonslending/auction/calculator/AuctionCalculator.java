package pl.fintech.dragons.dragonslending.auction.calculator;

import pl.fintech.dragons.dragonslending.auction.Auction;
import pl.fintech.dragons.dragonslending.auction.dto.CalculationDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AuctionCalculator {

  private static final double FEE_RATE = 0.02;

  public CalculationDto calculate(Auction entity) {

    BigDecimal finalValue = entity.getLoanAmount()
        .add(entity.getLoanAmount()
            .multiply(BigDecimal.valueOf(entity.getTimePeriod()))
            .multiply(BigDecimal.valueOf((entity.getInterestRate()/100)/12)))
        .add(entity.getLoanAmount()
            .multiply(BigDecimal.valueOf(FEE_RATE)));

    return CalculationDto.builder()
        .finalValue(finalValue.setScale(2, RoundingMode.HALF_UP))
        .periodValue(finalValue.divide(BigDecimal.valueOf(entity.getTimePeriod()),2,RoundingMode.HALF_UP))
        .build();
  }
}
