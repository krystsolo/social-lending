package pl.fintech.dragons.dragonslending.sociallending.offer

import org.apache.commons.lang3.RandomUtils
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.loanCalculator.LoanCalculatorFixtureData
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferQueryDto
import spock.lang.Specification

class OfferTest extends Specification {

    def "should return offer dto"() {
        given:
        Offer offer = OfferFixtureData.OFFER

        when:
        OfferQueryDto offerQueryDto = offer.toOfferDto(UserFixture.USER.username, LoanCalculatorFixtureData.CALCULATION_DTO)

        then:
        offer.id == offerQueryDto.id
        offer.offerAmount == offerQueryDto.offerAmount
        offer.interestRate == offerQueryDto.interestRate
        offerQueryDto.username == UserFixture.USER.username
        offerQueryDto.userId == UserFixture.USER.id
    }

    def "should return proper offer object"() {
        given:
        BigDecimal offerAmount = BigDecimal.TEN
        Integer timePeriod = RandomUtils.nextInt()
        Float interestRate = RandomUtils.nextFloat()


        when:
        Offer offer = new Offer(offerAmount, interestRate, timePeriod, AuctionFixtureData.AUCTION_ID, UserFixture.USER_ID)

        then:
        offer.offerAmount == BigDecimal.TEN
        offer.timePeriod == timePeriod
        offer.interestRate == interestRate
        offer.auctionId == AuctionFixtureData.AUCTION_ID
        offer.userId == UserFixture.USER_ID
    }
}
