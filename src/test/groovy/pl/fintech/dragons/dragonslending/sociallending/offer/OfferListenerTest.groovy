package pl.fintech.dragons.dragonslending.sociallending.offer

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionTerminated
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import spock.lang.Specification

class OfferListenerTest extends Specification {

    OfferRepository offerRepository = Mock(OfferRepository)
    EventPublisher eventPublisher = Mock(EventPublisher)
    OfferListener offerListener = new OfferListener(offerRepository, eventPublisher)

    def "Should change offer status to terminated when AuctionTerminated event received" () {
        given:
        AuctionTerminated auctionTerminated = AuctionTerminated.now(UserFixture.USER_ID, AuctionFixtureData.AUCTION_ID)

        when:
        offerListener.handleAutionTermination(auctionTerminated)

        then:
        1 * offerRepository.findAllByAuctionId(_ as UUID) >> []
    }

    def "Should change offer status to terminated for all offers not selected when OfferSelected event received"() {
        given:
        UUID offerId = UUID.randomUUID()

        when:
        offerListener.handleOfferSelection(OfferSelected.now(
                UUID.randomUUID(), offerId, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, 2.0, 5))

        then:
        1 * offerRepository.findAllByAuctionId(_ as UUID) >> []
    }
}
