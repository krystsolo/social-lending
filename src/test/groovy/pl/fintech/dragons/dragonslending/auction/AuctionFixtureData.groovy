package pl.fintech.dragons.dragonslending.auction

import pl.fintech.dragons.dragonslending.identity.UserFixture
import pl.fintech.dragons.dragonslending.auction.dto.CalculationDto
import pl.fintech.dragons.dragonslending.auction.dto.AuctionRequest

import java.time.LocalDate

class AuctionFixtureData {

    static final UUID AUCTION_ID = UUID.randomUUID()

    static final LocalDate DATE = LocalDate.now().plusDays(2)

    static final Auction AUCTION = new Auction(BigDecimal.valueOf(1000), 2, 2.5, DATE, UserFixture.USER_ID)

    static final CALCULATION_DTO = new CalculationDto(BigDecimal.valueOf(1024.17), 512.08)

    static final List<Auction> AUCTION_LIST = [AUCTION, AUCTION]

    static final AuctionRequest AUCTION_REQUEST = new AuctionRequest(
            AUCTION_ID, BigDecimal.valueOf(1000), 2, 2.5, DATE)
}
