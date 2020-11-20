package pl.fintech.dragons.dragonslending.sociallending.auction.calculator

import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.CalculationDto;
import spock.lang.Specification;

class AuctionCalculatorTest extends Specification {

  def "should return proper result of calculation" () {
    given:
    AuctionCalculator auctionCalculator = new AuctionCalculator();

    when:
    CalculationDto calculationDto = auctionCalculator.calculate(AuctionFixtureData.AUCTION)

    then:
    calculationDto.finalValue == AuctionFixtureData.CALCULATION_DTO.finalValue
    calculationDto.periodValue == AuctionFixtureData.CALCULATION_DTO.periodValue
  }
}
