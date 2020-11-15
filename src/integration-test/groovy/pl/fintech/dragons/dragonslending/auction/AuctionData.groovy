package pl.fintech.dragons.dragonslending.auction

import pl.fintech.dragons.dragonslending.auction.dto.AuctionRequest

import java.time.LocalDate

class AuctionData {
    static final UUID AUCTION_ID = UUID.randomUUID()
    static final LocalDate DATE = LocalDate.now().plusDays(1)

    static final AuctionRequest AUCTION_REQUEST = new AuctionRequest(
            AUCTION_ID, BigDecimal.valueOf(1000), 2, 2.5, DATE)
}
