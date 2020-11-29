package pl.fintech.dragons.dragonslending.sociallending.auction

import pl.fintech.dragons.dragonslending.common.events.EventPublisher
import pl.fintech.dragons.dragonslending.common.exceptions.ResourceNotFoundException
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.Auction
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionRepository
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionStatus
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionTerminated
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import spock.lang.Specification

import java.nio.file.AccessDeniedException

import static pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData.*

class AuctionServiceTest extends Specification {
    AuctionRepository auctionRepository = Mock(AuctionRepository)
    UserService userService = Mock(UserService)
    EventPublisher eventPublisher = Mock(EventPublisher)
    AuctionService auctionService = new AuctionService(auctionRepository, userService, eventPublisher)

    def "Should get auction by id"() {
        given:
        mockUserById()
        auctionRepository.findByIdAndAuctionStatus(AUCTION_ID, AuctionStatus.ACTIVE) >> Optional.ofNullable(AUCTION)

        when:
        def auctionQueryDto = auctionService.getAuction(AUCTION_ID)

        then:
        auctionQueryDto == AUCTION.toAuctionDto(UserFixture.USER_DTO.username)
    }

    def "Should throw exception when auction repository doesn't return auction" () {
        given:
        auctionRepository.findByIdAndAuctionStatus(AUCTION_ID, AuctionStatus.ACTIVE) >> Optional.ofNullable(null)

        when:
        auctionService.getAuction(AUCTION_ID)

        then:
        thrown(ResourceNotFoundException)
    }

    def "Should return list of all auctions without current logged user auctions"() {
        given:
        mockCurrentLoggedUser()
        mockUserById()
        auctionRepository.findAllByUserIdIsNotAndAuctionStatus(UserFixture.USER_ID, AuctionStatus.ACTIVE) >> AUCTION_LIST

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
        auctionRepository.findAllByUserIdAndAuctionStatus(UserFixture.USER_ID, AuctionStatus.ACTIVE) >> AUCTION_LIST

        when:
        def auctionQueryDto = auctionService.getCurrentUserAuctions()

        then:
        auctionQueryDto == AUCTION_QUERY_LIST
        and:
        auctionQueryDto.size() == 2
    }

    def "Should return list of all public auctions"() {
        given:
        mockCurrentLoggedUser()
        mockUserById()
        auctionRepository.findAllByAuctionStatus(AuctionStatus.ACTIVE) >> AUCTION_LIST

        when:
        def auctionQueryDto = auctionService.getPublicAuctions()

        then:
        auctionQueryDto == AUCTION_QUERY_LIST
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
        auctionRepository.getOne(AUCTION_ID) >> new Auction(BigDecimal.valueOf(1000), 2, 2.5, DATE, UserFixture.USER_ID)

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
        1 * eventPublisher.publish(_ as AuctionTerminated)
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
