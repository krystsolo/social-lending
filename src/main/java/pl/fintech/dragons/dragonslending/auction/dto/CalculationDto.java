package pl.fintech.dragons.dragonslending.auction.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Builder
@Value
public class CalculationDto {
  BigDecimal finalValue;
  BigDecimal periodValue;
}
