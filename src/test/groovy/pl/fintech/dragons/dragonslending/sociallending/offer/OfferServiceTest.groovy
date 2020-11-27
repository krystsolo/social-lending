package pl.fintech.dragons.dragonslending.sociallending.offer

import org.springframework.security.access.AccessDeniedException
import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionService
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.LoanCalculationService
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculation
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferQueryDto
import spock.lang.Specification

class OfferServiceTest extends Specification {

    OfferRepository offerRepository = Mock(OfferRepository)
    UserService userService = Mock(UserService)
    LoanCalculationService loanCalculationService = Mock(LoanCalculationService)
    AuctionService auctionService = Mock(AuctionService)
    EventPublisher eventPublisher = Mock(EventPublisher)
    OfferService offerService = new OfferService(offerRepository, loanCalculationService, userService, auctionService, eventPublisher)

    def "Should return list of offers current logged user"() {
        given:
        mockCurrentLoggedUser()
        mockUserById()
        mockLoanCalculator()
        mockGetAuction()
        offerRepository.findAllByUserIdAndOfferStatus(UserFixture.USER_ID, OfferStatus.ACTIVE) >> OfferFixtureData.OFFER_LIST
        List<OfferQueryDto> offerList = OfferFixtureData.OFFER_QUERY_LIST

        when:
        def offerQueryDto = offerService.getCurrentLoggedUserOffers()

        then:
        offerQueryDto == offerList
        and:
        offerQueryDto.size() == 2
    }

    def "Should return list of offers by auction id"() {
        given:
        mockUserById()
        mockLoanCalculator()
        mockGetAuction()
        offerRepository.findAllByAuctionId(AuctionFixtureData.AUCTION_ID) >> OfferFixtureData.OFFER_LIST

        when:
        def offerQueryDto = offerService.getOffersByAuctionId(AuctionFixtureData.AUCTION_ID)

        then:
        offerQueryDto == OfferFixtureData.OFFER_QUERY_LIST
        and:
        offerQueryDto.size() == 2
    }

    def "Should create new offer"() {
        given:
        mockCurrentLoggedUser()
        mockGetAuction()
        offerRepository.findByAuctionIdAndUserId(AuctionFixtureData.AUCTION_ID, UserFixture.USER_ID) >> Optional.ofNullable(null)

        when:
        def offerId = offerService.saveOffer(OfferFixtureData.OFFER_REQUEST)

        then:
        offerId != null
        and:
        1 * eventPublisher.publish(_ as OfferSubmitted)
    }

    def "Should throw illegal argument exception during create offer when auction id is null"() {
        when:
        offerService.saveOffer(OfferFixtureData.OFFER_REQUEST)

        then:
        thrown(IllegalArgumentException)
    }

    def "Should throw illegal argument exception during create offer when we try add again offer to this same auction"() {
        given:
        mockGetAuction()
        mockCurrentLoggedUser()
        offerRepository.findByAuctionIdAndUserId(AuctionFixtureData.AUCTION_ID, UserFixture.USER_ID) >> Optional.ofNullable(OfferFixtureData.OFFER)


        when:
        offerService.saveOffer(OfferFixtureData.OFFER_REQUEST)

        then:
        thrown(IllegalArgumentException)
    }

    def "Should delete offer"() {
        given:
        mockRepositoryGetOne()
        mockCurrentLoggedUser()

        when:
        offerService.deleteOffer(OfferFixtureData.OFFER_ID)


        then:
        OfferFixtureData.OFFER.offerStatus == OfferStatus.TERMINATED
        and:
        1 * eventPublisher.publish(_ as OfferTerminated)
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

    def "Should select offer from auction"() {
        given:
        offerRepository.findById(OfferFixtureData.OFFER_ID) >> Optional.of(OfferFixtureData.OFFER)
        mockCurrentLoggedUser()
        mockGetAuction()
        offerRepository.findByAuctionIdAndUserId(AuctionFixtureData.AUCTION_ID, UserFixture.USER_ID) >> Optional.ofNullable(null)

        when:
        offerService.selectOffer(OfferFixtureData.OFFER_ID)

        then:
        1 * eventPublisher.publish(_ as OfferSelected)
    }

    void mockRepositoryGetOne() {
        offerRepository.getOne(OfferFixtureData.OFFER_ID) >> OfferFixtureData.OFFER
    }

    void mockLoanCalculator() {
        loanCalculationService.calculateAmountsToRepaid(OfferFixtureData.OFFER.offerAmount, OfferFixtureData.OFFER.timePeriod, OfferFixtureData.OFFER.interestRate) >> new LoanCalculation(BigDecimal.TEN, BigDecimal.TEN)
    }

    void mockUserById() {
        userService.getUser(UserFixture.USER_ID) >> UserFixture.USER_DTO
    }

    void mockCurrentLoggedUser() {
        userService.getCurrentLoggedUser() >> UserFixture.USER_DTO
    }

    void mockGetAuction() {
        auctionService.getAuction(OfferFixtureData.OFFER.auctionId) >> AuctionFixtureData.AUCTION_QUERY
    }
}
