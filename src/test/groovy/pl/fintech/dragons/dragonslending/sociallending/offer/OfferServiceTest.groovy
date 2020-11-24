package pl.fintech.dragons.dragonslending.sociallending.offer

import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionService
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import pl.fintech.dragons.dragonslending.sociallending.loanCalculator.LoanCalculator
import pl.fintech.dragons.dragonslending.sociallending.loanCalculator.LoanCalculatorFixtureData
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferQueryDto
import spock.lang.Specification

import java.nio.file.AccessDeniedException

class OfferServiceTest extends Specification {
    OfferRepository offerRepository = Mock(OfferRepository)
    UserService userService = Mock(UserService)
    LoanCalculator loanCalculator = Mock(LoanCalculator)
    AuctionService auctionService = Mock(AuctionService)
    OfferService offerService = new OfferService(offerRepository, loanCalculator, userService, auctionService)


    def "Should return list of offers current logged user"() {
        given:
        mockCurrentLoggedUser()
        mockUserById()
        mockLoanCalculator()
        List<OfferQueryDto> offerList = [OfferFixtureData.OFFER.toOfferDto(UserFixture.USER_DTO.username, LoanCalculatorFixtureData.CALCULATION_DTO),
                                         OfferFixtureData.OFFER.toOfferDto(UserFixture.USER_DTO.username, LoanCalculatorFixtureData.CALCULATION_DTO)]
        offerRepository.findAllByUserId(UserFixture.USER_ID) >> OfferFixtureData.OFFER_LIST

        when:
        def offerQueryDto = offerService.getCurrentLoggedUserOffers()

        then:
        offerQueryDto == offerList
        and:
        offerQueryDto.size() == 2
    }

    def "Should return list of offers by auction id"() {
        given:
        mockCurrentLoggedUser()
        mockUserById()
        mockLoanCalculator()
        List<OfferQueryDto> offerList = [OfferFixtureData.OFFER.toOfferDto(UserFixture.USER_DTO.username, LoanCalculatorFixtureData.CALCULATION_DTO),
                                         OfferFixtureData.OFFER.toOfferDto(UserFixture.USER_DTO.username, LoanCalculatorFixtureData.CALCULATION_DTO)]
        offerRepository.findAllByAuctionId(AuctionFixtureData.AUCTION_ID) >> OfferFixtureData.OFFER_LIST

        when:
        def offerQueryDto = offerService.getOffersByAuctionId(AuctionFixtureData.AUCTION_ID)

        then:
        offerQueryDto == offerList
        and:
        offerQueryDto.size() == 2
    }

    def "Should create new offer"() {
        given:
        mockCurrentLoggedUser()
        auctionService.getAuction(AuctionFixtureData.AUCTION_ID) >> AuctionFixtureData.AUCTION_QUERY

        when:
        def offerId = offerService.saveOffer(OfferFixtureData.OFFER_REQUEST)

        then:
        offerId != null
    }

    def "Should throw illegal argument exception during create offer when auction id is null"() {
        when:
        def offerId = offerService.saveOffer(OfferFixtureData.OFFER_REQUEST)

        then:
        thrown(IllegalArgumentException)
    }

    def "Should delete offer"() {
        given:
        mockRepositoryGetOne()
        userService.getCurrentLoggedUser() >> UserFixture.USER_DTO

        when:
        offerService.deleteOffer(OfferFixtureData.OFFER_ID)


        then:
        1 * offerRepository.deleteById(OfferFixtureData.OFFER_ID)
    }


    def "Should throw access denied exception during deleting offer when this offer is not assign to current logged user"() {
        given:
        mockRepositoryGetOne()
        userService.getCurrentLoggedUser() >> UserDto.builder().id(UUID.randomUUID()).build()

        when:
        offerService.deleteOffer(OfferFixtureData.OFFER_ID)

        then:
        thrown(AccessDeniedException)
    }


    void mockRepositoryGetOne() {
        offerRepository.getOne(OfferFixtureData.OFFER_ID) >> OfferFixtureData.OFFER
    }

    void mockLoanCalculator() {
        loanCalculator.calculate(OfferFixtureData.OFFER.offerAmount, OfferFixtureData.OFFER.timePeriod, OfferFixtureData.OFFER.interestRate) >> LoanCalculatorFixtureData.CALCULATION_DTO
    }

    void mockUserById() {
        userService.getUser(UserFixture.USER_ID) >> UserFixture.USER_DTO
    }

    void mockCurrentLoggedUser() {
        userService.getCurrentLoggedUser() >> UserFixture.USER_DTO
    }
}
