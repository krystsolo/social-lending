package pl.fintech.dragons.dragonslending.auction.calculator

import pl.fintech.dragons.dragonslending.auction.AuctionFixture
import pl.fintech.dragons.dragonslending.auction.dto.CalculationDto;
import spock.lang.Specification;

class AuctionCalculatorTest extends Specification {

  def "should return proper result of calculation" () {
    given:
    AuctionCalculator auctionCalculator = new AuctionCalculator();

    when:
    CalculationDto calculationDto = auctionCalculator.calculate(AuctionFixture.AUCTION)

    then:
    calculationDto.finalValue == AuctionFixture.CALCULATION_DTO.finalValue
    calculationDto.periodValue == AuctionFixture.CALCULATION_DTO.periodValue
  }
}
