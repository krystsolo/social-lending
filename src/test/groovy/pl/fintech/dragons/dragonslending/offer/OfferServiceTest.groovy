package pl.fintech.dragons.dragonslending.offer

import pl.fintech.dragons.dragonslending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.identity.application.UserService
import pl.fintech.dragons.dragonslending.offer.calculator.OfferCalculator
import pl.fintech.dragons.dragonslending.offer.dto.OfferQueryDto
import pl.fintech.dragons.dragonslending.offer.dto.OfferRequest
import spock.lang.Specification

import java.nio.file.AccessDeniedException

import static pl.fintech.dragons.dragonslending.identity.UserFixture.*
import static pl.fintech.dragons.dragonslending.offer.OfferFixture.*

class OfferServiceTest extends Specification {
    OfferRepository offerRepository = Mock(OfferRepository)
    OfferCalculator offerCalculator = Mock(OfferCalculator)
    UserService userService = Mock(UserService)
    OfferService offerService = new OfferService(offerRepository, offerCalculator, userService)

    def "should get offerQueryDto by id"() {
        given:
        mockUserById()
        mockRepositoryGetOne()
        mockOfferCalculator()

        when:
        def offerQueryDto = offerService.getOffer(OFFER_ID)

        then:
        offerQueryDto == OFFER.toOfferQueryDto(CALCULATION_DTO, USER_DTO.username)
    }

    def "should return list of offerQueryDto"() {
        given:
        List<OfferQueryDto> offersList = [OFFER.toOfferQueryDto(CALCULATION_DTO, USER_DTO.username), OFFER.toOfferQueryDto(CALCULATION_DTO, USER_DTO.username)]
        mockUserById()
        mockRepositoryFindAll()
        mockOfferCalculator()

        when:
        def offerQueryDto = offerService.getOffers()

        then:
        offerQueryDto == offersList
        and:
        offerQueryDto.size() == 2
    }


    def "should create new offer"() {
        given:
        mockCurrentLoggedUser()

        when:
        def offerId = offerService.saveOfferDto(OFFER_REQUEST)

        then:
        offerId != null
    }

    def "should update offer"() {
        given:
        mockCurrentLoggedUser()
        mockRepositoryGetOne()

        when:
        def offerId = offerService.updateOfferDto(OFFER_REQUEST)

        then:
        offerId != null
    }

    def "should throw illegal argument exception during update offer when offer id is null"() {
        given:
        OfferRequest offerRequest = OfferRequest.builder()
                .loanAmount(OFFER_REQUEST.loanAmount)
                .timePeriod(OFFER_REQUEST.timePeriod)
                .interestRate(OFFER_REQUEST.interestRate)
                .endDate(OFFER_REQUEST.endDate)
                .build()

        when:
        offerService.updateOfferDto(offerRequest)

        then:
        thrown(IllegalArgumentException)
    }

    def "should throw access denied exception during update offer when this offer is not assign to current logged user"() {
        given:
        mockRepositoryGetOne()
        userService.getCurrentLoggedUser() >> UserDto.builder().id(UUID.randomUUID()).build()

        when:
        offerService.updateOfferDto(OFFER_REQUEST)

        then:
        thrown(AccessDeniedException)
    }


    void mockRepositoryGetOne() {
        offerRepository.getOne(OFFER_ID) >> OFFER
    }

    void mockRepositoryFindAll() {
        offerRepository.findAll() >> OFFER_LIST
    }

    void mockOfferCalculator() {
        offerCalculator.calculate(OFFER) >> CALCULATION_DTO
    }

    void mockUserById() {
        userService.getUser(USER_ID) >> USER_DTO
    }

    void mockCurrentLoggedUser() {
        userService.getCurrentLoggedUser() >> USER_DTO
    }
}
