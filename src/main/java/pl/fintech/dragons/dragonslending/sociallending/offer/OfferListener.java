package pl.fintech.dragons.dragonslending.sociallending.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionTerminated;
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.*;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.FrozenMoneyReleased;

@RequiredArgsConstructor
public class OfferListener {

    private final OfferRepository offerRepository;
    private final EventPublisher eventPublisher;

    @EventListener
    public void handleAuctionTermination(AuctionTerminated event) {
        offerRepository.findAllByAuctionIdAndOfferStatus(event.getAuctionId(), OfferStatus.ACTIVE)
                .forEach(this::terminateOffer);
    }

    @EventListener
    public void handleOfferSelection(OfferSelected event) {
        offerRepository.findAllByAuctionIdAndOfferStatus(event.getAuctionId(), OfferStatus.ACTIVE)
                .stream()
                .filter(offer -> !offer.getId().equals(event.getOfferId()))
                .forEach(this::terminateOffer);
    }

    @EventListener
    public void handleFrozenMoneyReleased(FrozenMoneyReleased event) {
        offerRepository.findAllByUserIdAndOfferStatus(event.getUserId(), OfferStatus.ACTIVE)
                .forEach(this::terminateOffer);
    }

    private void terminateOffer(Offer offer) {
        offer.makeOfferTerminated();
        eventPublisher.publish(OfferTerminated.now(offer.getAuctionId(), offer.getId(), offer.getUserId(), offer.getOfferAmount()));
    }
}
