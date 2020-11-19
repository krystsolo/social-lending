package pl.fintech.dragons.dragonslending.auction

import org.apache.commons.lang3.RandomUtils
import pl.fintech.dragons.dragonslending.auction.dto.AuctionQueryDto
import pl.fintech.dragons.dragonslending.auction.dto.AuctionRequest
import pl.fintech.dragons.dragonslending.auction.dto.CalculationDto
import pl.fintech.dragons.dragonslending.identity.UserData

import java.time.LocalDate

class AuctionDataFictureFactory {
    static final UUID AUCTION_ID = UUID.randomUUID()
    static final LocalDate DATE = LocalDate.now().plusDays(1)

    static final AuctionRequest AUCTION_REQUEST = new AuctionRequest(
            AUCTION_ID, BigDecimal.valueOf(RandomUtils.nextInt(0, 10000)), RandomUtils.nextInt(1, 36), RandomUtils.nextFloat(0, 20), DATE)

    static final AuctionQueryDto AUCTION_QUERY_DTO = new AuctionQueryDto(
            AUCTION_ID, BigDecimal.valueOf(RandomUtils.nextInt(0, 10000)), RandomUtils.nextInt(1, 36), RandomUtils.nextFloat(0, 20), DATE,
            new CalculationDto(BigDecimal.valueOf(RandomUtils.nextInt(0, 10000)), BigDecimal.valueOf(RandomUtils.nextInt(0, 10000))), UserData.USER_ID, UserData.USER.username)

    static final Auction AUCTION = new Auction(BigDecimal.valueOf(RandomUtils.nextInt(0, 10000)), RandomUtils.nextInt(1, 36), RandomUtils.nextFloat(0, 20), DATE, UserData.USER_ID)

    static final List<AuctionQueryDto> AUCTION_QUERY_LIST = [AUCTION_QUERY_DTO, AUCTION_QUERY_DTO]
}
