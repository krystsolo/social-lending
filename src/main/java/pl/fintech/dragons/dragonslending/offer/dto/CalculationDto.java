package pl.fintech.dragons.dragonslending.offer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
public class CalculationDto {
  private BigDecimal finalValue;
  private BigDecimal periodValue;
}
