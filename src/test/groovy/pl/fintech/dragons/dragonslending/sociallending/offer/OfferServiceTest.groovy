package pl.fintech.dragons.dragonslending.sociallending.offer

import org.springframework.security.access.AccessDeniedException
import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.common.exceptions.ResourceNotFoundException
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionService
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.LoanCalculationService
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculation
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.Offer
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferRepository
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferSelected
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferStatus
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferSubmitted
import pl.fintech.dragons.dragonslending.sociallending.offer.domain.OfferTerminated
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
        offerRepository.findAllByAuctionIdAndOfferStatus(AuctionFixtureData.AUCTION_ID, OfferStatus.ACTIVE) >> OfferFixtureData.OFFER_LIST

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
        offerRepository.findByAuctionIdAndUserIdAndOfferStatus(AuctionFixtureData.AUCTION_ID, UserFixture.USER_ID, OfferStatus.ACTIVE) >> Optional.ofNullable(null)

        when:
        def offerId = offerService.saveOffer(OfferFixtureData.OFFER_REQUEST)

        then:
        offerId != null
        and:
        1 * eventPublisher.publish(_ as OfferSubmitted)
    }

    def "Should throw illegal argument exception during create offer when we try add again offer to this same auction"() {
        given:
        mockGetAuction()
        mockCurrentLoggedUser()
        offerRepository.findByAuctionIdAndUserIdAndOfferStatus(AuctionFixtureData.AUCTION_ID, UserFixture.USER_ID, OfferStatus.ACTIVE) >> Optional.ofNullable(OfferFixtureData.OFFER)


        when:
        offerService.saveOffer(OfferFixtureData.OFFER_REQUEST)

        then:
        thrown(IllegalArgumentException)
    }

    def "Should delete offer"() {
        given:
        mockRepositoryFindById()
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
        mockRepositoryFindById()
        userService.getCurrentLoggedUser() >> UserDto.builder().id(UUID.randomUUID()).build()

        when:
        offerService.deleteOffer(OfferFixtureData.OFFER_ID)

        then:
        thrown(AccessDeniedException)
    }

    def "Should throw resource not found exception during deleting offer when not found offer in database" () {
        given:
        offerRepository.findById(OfferFixtureData.OFFER_ID) >> Optional.ofNullable(null)

        when:
        offerService.deleteOffer(OfferFixtureData.OFFER_ID)

        then:
        thrown(ResourceNotFoundException)
    }

    def "Should select offer from auction"() {
        given:
        offerRepository.findById(OfferFixtureData.OFFER_ID) >> Optional.of(new Offer(BigDecimal.valueOf(1000), 2.5, 2, AuctionFixtureData.AUCTION_ID, UserFixture.USER_ID))
        mockCurrentLoggedUser()
        mockGetAuction()
        offerRepository.findByAuctionIdAndUserIdAndOfferStatus(AuctionFixtureData.AUCTION_ID, UserFixture.USER_ID, OfferStatus.ACTIVE) >> Optional.ofNullable(null)

        when:
        offerService.selectOffer(OfferFixtureData.OFFER_ID)

        then:
        1 * eventPublisher.publish(_ as OfferSelected)
    }

    def "Should return list of received offers" () {
        given :
        mockUserById()
        mockLoanCalculator()
        mockGetAuction()
        auctionService.getCurrentUserAuctions() >> AuctionFixtureData.AUCTION_QUERY_LIST
        offerRepository.findAllByAuctionIdInAndOfferStatus(_ as List<UUID>, OfferStatus.ACTIVE) >> OfferFixtureData.OFFER_LIST

        when:
        def received = offerService.getListReceivedOffers()

        then:
        received == OfferFixtureData.OFFER_QUERY_LIST
    }

    void mockRepositoryFindById() {
        offerRepository.findById(OfferFixtureData.OFFER_ID) >> Optional.ofNullable(OfferFixtureData.OFFER)
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
