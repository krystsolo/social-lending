package pl.fintech.dragons.dragonslending.offer.calculator

import pl.fintech.dragons.dragonslending.offer.OfferFixture
import pl.fintech.dragons.dragonslending.offer.dto.CalculationDto;
import spock.lang.Specification;

class OfferCalculatorTest extends Specification {

  def "should return proper result of calculation" () {
    given:
    OfferCalculator offerCalculator = new OfferCalculator();

    when:
    CalculationDto calculationDto = offerCalculator.calculate(OfferFixture.OFFER)

    then:
    calculationDto.finalValue == OfferFixture.CALCULATION_DTO.finalValue
    calculationDto.periodValue == OfferFixture.CALCULATION_DTO.periodValue
  }
}
