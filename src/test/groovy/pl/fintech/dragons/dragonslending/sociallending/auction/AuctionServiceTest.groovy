package pl.fintech.dragons.dragonslending.sociallending.auction

import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionRequest
import pl.fintech.dragons.dragonslending.sociallending.identity.UserFixture
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import spock.lang.Specification

import java.nio.file.AccessDeniedException

import static pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData.*
import static pl.fintech.dragons.dragonslending.sociallending.auction.AuctionFixtureData.AUCTION_ID

class AuctionServiceTest extends Specification {
    AuctionRepository auctionRepository = Mock(AuctionRepository)
    UserService userService = Mock(UserService)
    AuctionService auctionService = new AuctionService(auctionRepository, userService)

    def "Should get auction by id"() {
        given:
        mockUserById()
        mockRepositoryGetOne()

        when:
        def auctionQueryDto = auctionService.getAuction(AUCTION_ID)

        then:
        auctionQueryDto == AUCTION.toAuctionDto(UserFixture.USER_DTO.username)
    }

    def "Should return list of all auctions"() {
        given:
        mockUserById()
        List<AuctionQueryDto> auctionList = [AUCTION.toAuctionDto(UserFixture.USER_DTO.username),
                                             AUCTION.toAuctionDto(UserFixture.USER_DTO.username)]
        auctionRepository.findAll() >> AUCTION_LIST

        when:
        def auctionQueryDto = auctionService.getPublicAuctions()

        then:
        auctionQueryDto == auctionList
        and:
        auctionQueryDto.size() == 2
    }

    def "Should return list of all current logged user auctions"() {
        given:
        mockCurrentLoggedUser()
        List<AuctionQueryDto> auctionList = [
                AUCTION.toAuctionDto(UserFixture.USER_DTO.username),
                AUCTION.toAuctionDto(UserFixture.USER_DTO.username)]
        auctionRepository.findAllByUserId(UserFixture.USER_ID) >> AUCTION_LIST
        userService.getUser(UserFixture.USER_ID) >> UserFixture.USER_DTO

        when:
        def auctionQueryDto = auctionService.getCurrentUserAuctions()

        then:
        auctionQueryDto == auctionList
        and:
        auctionQueryDto.size() == 2
    }

    def "Should return list of all auctions without current logged user auctions"() {
        given:
        mockCurrentLoggedUser()
        List<AuctionQueryDto> auctionList = [
                AUCTION.toAuctionDto(UserFixture.USER_DTO.username),
                AUCTION.toAuctionDto(UserFixture.USER_DTO.username)]
        auctionRepository.findAllByUserIdIsNot(UserFixture.USER_ID) >> AUCTION_LIST
        userService.getUser(UserFixture.USER_ID) >> UserFixture.USER_DTO

        when:
        def auctionQueryDto = auctionService.getAllNotCurrentUserAuctions()

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
        userService.getCurrentLoggedUser() >> UserFixture.USER_DTO

        when:
        auctionService.deleteAuction(AUCTION_ID)


        then:
        1 * auctionRepository.deleteById(AUCTION_ID)
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
