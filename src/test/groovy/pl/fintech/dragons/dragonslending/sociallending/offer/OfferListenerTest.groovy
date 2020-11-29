package pl.fintech.dragons.dragonslending.sociallending.offer

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionTerminated
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferRepository
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferSelected
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferStatus
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.FrozenMoneyReleased
import spock.lang.Specification

class OfferListenerTest extends Specification {

    OfferRepository offerRepository = Mock(OfferRepository)
    EventPublisher eventPublisher = Mock(EventPublisher)
    OfferListener offerListener = new OfferListener(offerRepository, eventPublisher)

    def "Should change offer status to terminated when AuctionTerminated event received" () {
        given:
        AuctionTerminated auctionTerminated = AuctionTerminated.now(UserFixture.USER_ID, AuctionFixtureData.AUCTION_ID)

        when:
        offerListener.handleAuctionTermination(auctionTerminated)

        then:
        1 * offerRepository.findAllByAuctionIdAndOfferStatus(_ as UUID, OfferStatus.ACTIVE) >> []
    }

    def "Should change offer status to terminated for all offers not selected when OfferSelected event received"() {
        given:
        UUID offerId = UUID.randomUUID()

        when:
        offerListener.handleOfferSelection(OfferSelected.now(
                UUID.randomUUID(), offerId, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, 2.0, 5))

        then:
        1 * offerRepository.findAllByAuctionIdAndOfferStatus(_ as UUID, OfferStatus.ACTIVE) >> []
    }

    def "Should change offer status to terminated for all user offers for whose frozen money was released"() {
        given:
        UUID userId = UUID.randomUUID()

        when:
        offerListener.handleFrozenMoneyReleased(FrozenMoneyReleased.now(userId, UUID.randomUUID()))

        then:
        1 * offerRepository.findAllByUserIdAndOfferStatus(_, _) >> []
    }
}
