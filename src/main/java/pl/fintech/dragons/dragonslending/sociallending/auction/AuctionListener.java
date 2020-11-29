package pl.fintech.dragons.dragonslending.sociallending.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.Auction;
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionRepository;
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferSelected;

@RequiredArgsConstructor
public class AuctionListener {

    private final AuctionRepository auctionRepository;

    @EventListener
    public void handleOfferSelection(OfferSelected event) {
        auctionRepository.findById(event.getAuctionId())
                .ifPresent(Auction::makeAuctionTerminated);
    }
}
