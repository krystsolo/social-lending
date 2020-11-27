package pl.fintech.dragons.dragonslending.sociallending.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionTerminated;

@RequiredArgsConstructor
public class OfferListener {

    private final OfferRepository offerRepository;
    private final EventPublisher eventPublisher;

    @EventListener
    public void handleAutionTermination(AuctionTerminated event) {
        offerRepository.findAllByAuctionId(event.getAuctionId())
                .forEach(this::terminateOffer);
    }

    @EventListener
    public void handleOfferSelection(OfferSelected event) {
        offerRepository.findAllByAuctionId(event.getAuctionId())
                .stream()
                .filter(offer -> !offer.getId().equals(event.getOfferId()))
                .forEach(this::terminateOffer);
    }

    private void terminateOffer(Offer offer) {
        offer.makeOfferTerminated();
        eventPublisher.publish(OfferTerminated.now(offer.getAuctionId(), offer.getId(), offer.getUserId(), offer.getOfferAmount()));
    }
}
