package pl.fintech.dragons.dragonslending.sociallending.auction

import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionRequest

import java.time.LocalDate

class AuctionFixtureData {

    static final UUID AUCTION_ID = UUID.randomUUID()

    static final LocalDate DATE = LocalDate.now().plusDays(2)

    static final Auction AUCTION = new Auction(BigDecimal.valueOf(1000), 2, 2.5, DATE, UserFixture.USER_ID)

    static final List<Auction> AUCTION_LIST = [AUCTION, AUCTION]

    static final AuctionQueryDto AUCTION_QUERY = AUCTION.toAuctionDto(UserFixture.USER.username)

    static final AuctionRequest AUCTION_REQUEST = new AuctionRequest(
            AUCTION_ID, BigDecimal.valueOf(1000), 2, 2.5, DATE)
}
