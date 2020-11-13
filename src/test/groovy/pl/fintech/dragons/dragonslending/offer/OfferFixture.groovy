package pl.fintech.dragons.dragonslending.offer

import pl.fintech.dragons.dragonslending.identity.UserFixture
import pl.fintech.dragons.dragonslending.offer.Offer
import pl.fintech.dragons.dragonslending.offer.dto.CalculationDto
import pl.fintech.dragons.dragonslending.offer.dto.OfferQueryDto
import pl.fintech.dragons.dragonslending.offer.dto.OfferRequest

import java.time.LocalDate

import static pl.fintech.dragons.dragonslending.identity.UserFixture.getUSER_DTO

class OfferFixture {

    static final UUID OFFER_ID = UUID.randomUUID()

    static final LocalDate DATE = LocalDate.now().plusDays(2)

    static final Offer OFFER = new Offer(BigDecimal.valueOf(1000), 2, 2.5, DATE, UserFixture.USER_ID)

    static final CALCULATION_DTO = new CalculationDto(BigDecimal.valueOf(1024.17), 512.08)

    static final List<Offer> OFFER_LIST = [OFFER, OFFER]

    static final OfferRequest OFFER_REQUEST = new OfferRequest(
            OFFER_ID, BigDecimal.valueOf(1000), 2, 2.5, DATE)
}
