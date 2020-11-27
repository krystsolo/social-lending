package pl.fintech.dragons.dragonslending.sociallending.offer.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Calculation {
    BigDecimal finalValue;
    BigDecimal periodValue;
}
