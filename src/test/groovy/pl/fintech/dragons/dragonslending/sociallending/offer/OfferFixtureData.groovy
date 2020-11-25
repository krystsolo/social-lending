package pl.fintech.dragons.dragonslending.sociallending.offer

import org.testcontainers.shaded.org.apache.commons.lang.math.RandomUtils
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.loanCalculator.LoanCalculatorFixtureData
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferQueryDto
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferRequest

class OfferFixtureData {
    static final UUID OFFER_ID = UUID.randomUUID()

    static final OfferRequest OFFER_REQUEST = new OfferRequest(BigDecimal.TEN, RandomUtils.nextFloat(), AuctionFixtureData.AUCTION_ID)

    static final Offer OFFER = new Offer(BigDecimal.valueOf(1000), 2.5, 2, AuctionFixtureData.AUCTION_ID, UserFixture.USER_ID)

    static final OfferQueryDto OFFER_QUERY = OFFER.toOfferDto(UserFixture.USER.username, LoanCalculatorFixtureData.CALCULATION_DTO, UserFixture.USER.username)

    static final List<Offer> OFFER_LIST = [OFFER, OFFER]

    static final List<OfferQueryDto> OFFER_QUERY_LIST = [OFFER_QUERY, OFFER_QUERY]
}
