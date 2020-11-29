package pl.fintech.dragons.dragonslending.sociallending.auction

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.Auction
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionRepository
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionStatus
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionTerminated
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class AuctionTerminatorTest extends Specification {

    AuctionRepository auctionRepository = Mock(AuctionRepository)
    EventPublisher eventPublisher = Mock(EventPublisher)

    AuctionTerminator auctionTerminator = new AuctionTerminator(auctionRepository, eventPublisher)

    def "should terminate all auctions which end date is today"() {
        given:
        auctionRepository.findAllByAuctionStatusAndEndDateBefore(AuctionStatus.ACTIVE, LocalDate.now()) >>
                [new Auction(UUID.randomUUID(), BigDecimal.TEN, 10, 5, LocalDate.now(), UUID.randomUUID(), AuctionStatus.ACTIVE, LocalDateTime.now())]

        when:
        auctionTerminator.handleTimeToSelectOfferForAuctionEnded()

        then:
        1 * eventPublisher.publish(_ as AuctionTerminated)
    }
}
