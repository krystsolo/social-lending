package pl.fintech.dragons.dragonslending

import org.apache.commons.lang3.RandomUtils
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.Auction
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionRequest
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.Offer
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferRequest

import java.time.LocalDate

class FictureDataFactory {
    static final LocalDate DATE = LocalDate.now().plusDays(1)

    static final AuctionRequest AUCTION_REQUEST = new AuctionRequest(BigDecimal.valueOf(RandomUtils.nextInt(0, 10000)), RandomUtils.nextInt(1, 36), RandomUtils.nextFloat(0, 20), DATE)

    static def auction(UUID userID) {
        new Auction(BigDecimal.valueOf(1000.0), RandomUtils.nextInt(1, 36),
                RandomUtils.nextFloat(0, 20), FictureDataFactory.DATE, userID)
    }

    static def offer(UUID auctionID, UUID userID) {
        new Offer(BigDecimal.valueOf(1000.0), RandomUtils.nextFloat(0, 20), RandomUtils.nextInt(1, 36), auctionID, userID)
    }
}
