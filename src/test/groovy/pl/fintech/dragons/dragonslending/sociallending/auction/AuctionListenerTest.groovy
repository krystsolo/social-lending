package pl.fintech.dragons.dragonslending.sociallending.auction

import pl.fintech.dragons.dragonslending.sociallending.auction.domain.Auction
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionRepository
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferSelected
import spock.lang.Specification

import java.time.LocalDate

class AuctionListenerTest extends Specification {
    AuctionRepository auctionRepository = Mock(AuctionRepository)
    AuctionListener auctionListener = new AuctionListener(auctionRepository)

    def "should handle offer selection event"() {
        given:
        UUID auctionId = UUID.randomUUID()

        when:
        auctionListener.handleOfferSelection(OfferSelected.now(
                auctionId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, 2.0, 5))

        then:
        1 * auctionRepository.findById(auctionId) >> Optional.of(new Auction(BigDecimal.TEN, 10, 2.0, LocalDate.now().plusDays(3), UUID.randomUUID()))
    }
}
