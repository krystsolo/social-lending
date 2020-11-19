package pl.fintech.dragons.dragonslending.auction

import pl.fintech.dragons.dragonslending.auction.dto.AuctionQueryDto
import pl.fintech.dragons.dragonslending.auction.dto.AuctionRequest
import pl.fintech.dragons.dragonslending.auction.dto.CalculationDto
import pl.fintech.dragons.dragonslending.identity.UserData

import java.time.LocalDate

class AuctionData {
    static final UUID AUCTION_ID = UUID.randomUUID()
    static final LocalDate DATE = LocalDate.now().plusDays(1)

    static final AuctionRequest AUCTION_REQUEST = new AuctionRequest(
            AUCTION_ID, BigDecimal.valueOf(1000), 2, 2.5, DATE)

    static final AuctionQueryDto AUCTION_QUERY_DTO = new AuctionQueryDto(
            AUCTION_ID, BigDecimal.valueOf(1000), 5, 3, DATE, new CalculationDto(BigDecimal.valueOf(1024.17), BigDecimal.valueOf(512.08)), UserData.USER_ID, UserData.USER.username)

    static final Auction AUCTION = new Auction(BigDecimal.valueOf(1000), 2, 2.5, DATE, UserData.USER_ID)

    static final List<AuctionQueryDto> AUCTION_QUERY_LIST = [AUCTION_QUERY_DTO, AUCTION_QUERY_DTO]
}
