package pl.fintech.dragons.dragonslending.auction

import pl.fintech.dragons.dragonslending.auction.dto.AuctionQueryDto
import spock.lang.Specification

import java.time.LocalDate

import static pl.fintech.dragons.dragonslending.auction.AuctionFixture.*
import static pl.fintech.dragons.dragonslending.identity.UserFixture.getUSER
import static pl.fintech.dragons.dragonslending.identity.UserFixture.getUSER_ID

class AuctionTest extends Specification {

    def "should return auction dto"() {
        given:
        Auction auction = AUCTION

        when:
        AuctionQueryDto auctionQueryDto = auction.toAuctionDto(CALCULATION_DTO, USER.username)

        then:
        auction.id == auctionQueryDto.id
        auction.loanAmount == auctionQueryDto.loanAmount
        auction.timePeriod == auctionQueryDto.timePeriod
        auction.interestRate == auctionQueryDto.interestRate
        auction.endDate == auctionQueryDto.endDate
        auctionQueryDto.calculation == CALCULATION_DTO
        auctionQueryDto.username == USER.username
        auctionQueryDto.userId == USER.id
    }

    def "should change auction parameters"() {
        given:
        Auction auction = AUCTION
        LocalDate date = LocalDate.now().plusDays(1)

        when:
        auction.changeAuctionParameters(BigDecimal.valueOf(2222), 5, 5, date);

        then:
        auction.id == AUCTION.id
        auction.loanAmount == BigDecimal.valueOf(2222)
        auction.timePeriod == 5
        auction.interestRate == 5
        auction.endDate == date
    }

    def "should return proper auction object"() {
        given:
        BigDecimal loanAmount = BigDecimal.valueOf(1111)
        Integer timePeriod = 2
        Float interestRate = 5.2


        when:
        Auction auction = new Auction(loanAmount, timePeriod, interestRate, DATE, USER_ID)

        then:
        auction.loanAmount == loanAmount
        auction.timePeriod == timePeriod
        auction.interestRate == interestRate
        auction.endDate == DATE
        auction.userId == USER_ID
    }
}
