package pl.fintech.dragons.dragonslending.offer


import pl.fintech.dragons.dragonslending.offer.dto.OfferQueryDto
import spock.lang.Specification

import java.time.LocalDate

import static pl.fintech.dragons.dragonslending.identity.UserFixture.*
import static pl.fintech.dragons.dragonslending.offer.OfferFixture.*

class OfferTest extends Specification {

    def "should return proper dto"() {
        given:
        Offer offer = OFFER

        when:
        OfferQueryDto offerQueryDto = offer.toOfferQueryDto(CALCULATION_DTO, USER.username)

        then:
        offer.id == offerQueryDto.id
        offer.loanAmount == offerQueryDto.loanAmount
        offer.timePeriod == offerQueryDto.timePeriod
        offer.interestRate == offerQueryDto.interestRate
        offer.endDate == offerQueryDto.endDate
        offerQueryDto.calculation == CALCULATION_DTO
        offerQueryDto.username == USER.username
        offerQueryDto.userId == USER.id
    }

    def "should change offer parameters"() {
        given:
        Offer offer = OFFER
        LocalDate date = LocalDate.now().plusDays(1)

        when:
        offer.changeOfferParameters(BigDecimal.valueOf(2222), 5, 5, date);

        then:
        offer.id == OFFER.id
        offer.loanAmount == BigDecimal.valueOf(2222)
        offer.timePeriod == 5
        offer.interestRate == 5
        offer.endDate == date
    }

    def "should return proper offer object"() {
        given:
        BigDecimal loanAmount = BigDecimal.valueOf(1111)
        Integer timePeriod = 2
        Float interestRate = 5.2


        when:
        Offer offer = new Offer(loanAmount, timePeriod, interestRate, DATE, USER_ID)

        then:
        offer.loanAmount == loanAmount
        offer.timePeriod == timePeriod
        offer.interestRate == interestRate
        offer.endDate == DATE
        offer.userId == USER_ID
    }
}
