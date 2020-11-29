package pl.fintech.dragons.dragonslending.sociallending.auction

import org.apache.commons.lang3.RandomUtils
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.Auction
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionStatus
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class AuctionTest extends Specification {

    def "Should return auction dto"() {
        given:
        Auction auction = AuctionFixtureData.AUCTION

        when:
        AuctionQueryDto auctionQueryDto = auction.toAuctionDto(UserFixture.USER.username)

        then:
        auction.id == auctionQueryDto.id
        auction.loanAmount == auctionQueryDto.loanAmount
        auction.timePeriod == auctionQueryDto.timePeriod
        auction.interestRate == auctionQueryDto.interestRate
        auction.endDate == auctionQueryDto.endDate
        auction.creationTime != null
        auction.auctionStatus == AuctionStatus.ACTIVE
        auctionQueryDto.username == UserFixture.USER.username
        auctionQueryDto.userId == UserFixture.USER.id
    }

    def "Should change proper auction parameters"() {
        given:
        def id = UUID.randomUUID()
        LocalDate date = LocalDate.now().plusDays(1)
        Auction auction = new Auction(id, BigDecimal.TEN, RandomUtils.nextInt(1, 36), RandomUtils.nextFloat(0, 20), date, UUID.randomUUID(), AuctionStatus.ACTIVE, LocalDateTime.now())

        when:
        auction.changeAuctionParameters(BigDecimal.valueOf(2222), 5, 5, date.plusDays(1));

        then:
        auction.id == id
        auction.loanAmount == BigDecimal.valueOf(2222)
        auction.timePeriod == 5
        auction.interestRate == 5
        auction.endDate == date.plusDays(1)
    }

    def "Should throw illegal state exception when auction status is terminated" () {
        given:
        Auction auction = new Auction(UUID.randomUUID(), BigDecimal.TEN, RandomUtils.nextInt(1, 36), RandomUtils.nextFloat(0, 20), LocalDate.now().plusDays(1), UUID.randomUUID(), AuctionStatus.TERMINATED, LocalDateTime.now())

        when:
        auction.changeAuctionParameters(BigDecimal.valueOf(2222), 5, 5, LocalDate.now().plusDays(1));

        then:
        thrown(IllegalStateException)
    }

    def "Should change auction status to terminated" () {
        given:
        Auction auction = AuctionFixtureData.AUCTION

        when:
        auction.makeAuctionTerminated()

        then:
        auction.auctionStatus == AuctionStatus.TERMINATED
    }

    def "Should return proper auction object"() {
        given:
        BigDecimal loanAmount = BigDecimal.valueOf(1111)
        Integer timePeriod = 2
        Float interestRate = 5.2

        when:
        Auction auction = new Auction(loanAmount, timePeriod, interestRate, AuctionFixtureData.DATE, UserFixture.USER_ID)

        then:
        auction.loanAmount == loanAmount
        auction.timePeriod == timePeriod
        auction.interestRate == interestRate
        auction.endDate == AuctionFixtureData.DATE
        auction.userId == UserFixture.USER_ID
    }
}
