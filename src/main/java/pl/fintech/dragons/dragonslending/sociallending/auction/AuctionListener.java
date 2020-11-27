package pl.fintech.dragons.dragonslending.sociallending.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferSelected;

@RequiredArgsConstructor
public class AuctionListener {

    private final AuctionRepository auctionRepository;

    @EventListener
    public void handleOfferSelection(OfferSelected event) {
        auctionRepository.findById(event.getAuctionId())
                .ifPresent(Auction::makeAuctionTerminated);
    }
}
