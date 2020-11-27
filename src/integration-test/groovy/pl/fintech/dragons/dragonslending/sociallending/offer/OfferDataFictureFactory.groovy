package pl.fintech.dragons.dragonslending.sociallending.offer

import org.apache.commons.lang3.RandomUtils
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionDataFictureFactory
import pl.fintech.dragons.dragonslending.sociallending.identity.UserData
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculation
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.Calculation
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferQueryDto
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferRequest

class OfferDataFictureFactory {
    static final UUID OFFER_ID = UUID.randomUUID()

    static final OfferRequest OFFER_REQUEST = new OfferRequest(BigDecimal.TEN, RandomUtils.nextFloat(0,20), AuctionDataFictureFactory.AUCTION_ID)

    static final Offer OFFER = new Offer(BigDecimal.valueOf(1000), 2.5, 2, AuctionDataFictureFactory.AUCTION_ID, UserData.USER_ID)

    static final LoanCalculation CALCULATION_DTO = new LoanCalculation(BigDecimal.valueOf(RandomUtils.nextInt(0, 1000000)), BigDecimal.valueOf(RandomUtils.nextInt(0, 1000000)))

    static final OfferQueryDto OFFER_QUERY = OFFER.toOfferDto(UserData.USER.username, new Calculation(BigDecimal.TEN, BigDecimal.TEN), UserFixture.USER.username)

    static final List<Offer> OFFER_LIST = [OFFER, OFFER]

    static final List<OfferQueryDto> OFFER_QUERY_LIST = [OFFER_QUERY, OFFER_QUERY]
}
