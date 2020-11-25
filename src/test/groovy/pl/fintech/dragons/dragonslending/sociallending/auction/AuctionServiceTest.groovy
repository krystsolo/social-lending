package pl.fintech.dragons.dragonslending.sociallending.auction

import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferFixtureData
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferService
import spock.lang.Specification

import java.nio.file.AccessDeniedException
import java.util.stream.Collectors

import static pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData.*
import static pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData.AUCTION_QUERY_LIST
import static pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData.AUCTION_QUERY_LIST

class AuctionServiceTest extends Specification {
    AuctionRepository auctionRepository = Mock(AuctionRepository)
    UserService userService = Mock(UserService)
    OfferService offerService = Mock(OfferService)
    AuctionService auctionService = new AuctionService(auctionRepository, userService, offerService)

    def "Should get auction by id"() {
        given:
        mockUserById()
        auctionRepository.findByIdAndAuctionStatus(AUCTION_ID, AuctionStatus.ACTIVE) >> AUCTION

        when:
        def auctionQueryDto = auctionService.getAuction(AUCTION_ID)

        then:
        auctionQueryDto == AUCTION.toAuctionDto(UserFixture.USER_DTO.username)
    }

    def "Should return list of all auctions without current logged user auctions"() {
        given:
        mockCurrentLoggedUser()
        mockUserById()

        List<UUID> auctionIds = new ArrayList<>(OfferFixtureData.OFFER_QUERY_LIST
                .stream().map({ e -> e.auctionId }).collect(Collectors.toList()))

        offerService.getCurrentLoggedUserOffers() >> OfferFixtureData.OFFER_QUERY_LIST

        auctionRepository.findAllByUserIdIsNotAndAuctionStatusAndIdIsNotIn(
                UserFixture.USER_ID, AuctionStatus.ACTIVE, auctionIds) >> AUCTION_LIST

        when:
        def auctionQueryDto = auctionService.getAllNotCurrentUserAuctions()

        then:
        auctionQueryDto == AUCTION_QUERY_LIST
        and:
        auctionQueryDto.size() == 2
    }

    def "Should return list of all current logged user auctions"() {
        given:
        mockUserById()
        mockCurrentLoggedUser()
        List<AuctionQueryDto> auctionList = AUCTION_QUERY_LIST

        auctionRepository.findAllByUserIdAndAuctionStatus(UserFixture.USER_ID, AuctionStatus.ACTIVE) >> AUCTION_LIST

        when:
        def auctionQueryDto = auctionService.getCurrentUserAuctions()

        then:
        auctionQueryDto == auctionList
        and:
        auctionQueryDto.size() == 2
    }

    def "Should return list of all public auctions"() {
        given:
        mockCurrentLoggedUser()
        mockUserById()
        List<AuctionQueryDto> auctionList = AUCTION_QUERY_LIST

        auctionRepository.findAllByAuctionStatus(AuctionStatus.ACTIVE) >> AUCTION_LIST

        when:
        def auctionQueryDto = auctionService.getPublicAuctions()

        then:
        auctionQueryDto == auctionList
        and:
        auctionQueryDto.size() == 2
    }

    def "Should create new auction"() {
        given:
        mockCurrentLoggedUser()

        when:
        def auctionId = auctionService.saveAuction(AUCTION_REQUEST)

        then:
        auctionId != null
    }

    def "Should update auction"() {
        given:
        mockCurrentLoggedUser()
        mockRepositoryGetOne()

        when:
        def auctionId = auctionService.updateAuction(AUCTION_REQUEST, AUCTION_ID)

        then:
        auctionId != null
    }

    def "Should throw access denied exception during update auction when this auction is not assign to current logged user"() {
        given:
        mockRepositoryGetOne()
        userService.getCurrentLoggedUser() >> UserDto.builder().id(UUID.randomUUID()).build()

        when:
        auctionService.updateAuction(AUCTION_REQUEST, AUCTION_ID)

        then:
        thrown(AccessDeniedException)
    }

    def "Should delete auction"() {
        given:
        mockRepositoryGetOne()
        mockCurrentLoggedUser()

        when:
        auctionService.deleteAuction(AUCTION_ID)


        then:
        1 * offerService.makeOffersTerminatedByAuction(AUCTION_ID)
        1 * auctionRepository.save(AUCTION)
    }

    def "Should throw access denied exception during deleting auction when this auction is not assign to current logged user"() {
        given:
        mockRepositoryGetOne()
        userService.getCurrentLoggedUser() >> UserDto.builder().id(UUID.randomUUID()).build()

        when:
        auctionService.deleteAuction(AUCTION_ID)

        then:
        thrown(AccessDeniedException)
    }


    void mockRepositoryGetOne() {
        auctionRepository.getOne(AUCTION_ID) >> AUCTION
    }

    void mockUserById() {
        userService.getUser(UserFixture.USER_ID) >> UserFixture.USER_DTO
    }

    void mockCurrentLoggedUser() {
        userService.getCurrentLoggedUser() >> UserFixture.USER_DTO
    }
}
