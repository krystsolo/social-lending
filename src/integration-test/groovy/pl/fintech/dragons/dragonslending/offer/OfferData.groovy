package pl.fintech.dragons.dragonslending.offer


import pl.fintech.dragons.dragonslending.offer.dto.OfferRequest

import java.time.LocalDate

class OfferData {
    static final UUID OFFER_ID = UUID.randomUUID()
    static final LocalDate DATE = LocalDate.now().plusDays(1)

    static final OfferRequest OFFER_REQUEST = new OfferRequest(
            OFFER_ID, BigDecimal.valueOf(1000), 2, 2.5, DATE)
}
