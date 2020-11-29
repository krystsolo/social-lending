package pl.fintech.dragons.dragonslending.sociallending.auction

import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.fintech.dragons.dragonslending.PostgreSQLContainerSpecification
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.Auction
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionRepository
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionStatus
import pl.fintech.dragons.dragonslending.sociallending.identity.UserData
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import pl.fintech.dragons.dragonslending.sociallending.identity.application.web.UserRegisterRequest
import pl.fintech.dragons.dragonslending.sociallending.identity.infrastructure.UserConfig
import pl.fintech.dragons.dragonslending.sociallending.auction.config.AuctionConfig
import spock.lang.Subject

import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [AuctionConfig.class, UserConfig.class])
class AuctionRepositoryIT extends PostgreSQLContainerSpecification {

    @Subject
    @Autowired
    AuctionRepository repository

    @Autowired
    UserService userService

    def cleanup() {
        repository.deleteAll()
    }

    def 'Should store new auction'() {
        given:
        UUID userUUID = addUserToDb()
        Auction auction = addAuctionToDb(userUUID, AuctionStatus.ACTIVE)

        when:
        repository.save(auction)

        then:
        def fromDb = repository.findAll()
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == auction.id
            loanAmount == auction.loanAmount
            timePeriod == auction.timePeriod
            interestRate == auction.interestRate
            endDate == auction.endDate
            userId == auction.userId
            auctionStatus == auction.auctionStatus
            creationTime != null
        }
    }

    def 'Should return auction by id'() {
        given:
        UUID userUUID = addUserToDb()
        Auction auction = addAuctionToDb(userUUID, AuctionStatus.ACTIVE)

        when:
        def fromDb = repository.getOne(auction.id)

        then:
        fromDb == auction
    }

    def 'Should return list of all auctions'() {
        given:
        UUID userUUID = addUserToDb()
        Auction auction1 =  addAuctionToDb(userUUID, AuctionStatus.ACTIVE)
        Auction auction2 =  addAuctionToDb(userUUID, AuctionStatus.ACTIVE)


        when:
        def fromDb = repository.findAll()

        then:
        fromDb.size() == 2

        and:
        with(fromDb.first()) {
            id == auction1.id
            loanAmount == auction1.loanAmount
            timePeriod == auction1.timePeriod
            interestRate == auction1.interestRate
            endDate == auction1.endDate
            userId == auction1.userId
            auctionStatus == auction1.auctionStatus
            creationTime != null
        }

        and:
        with(fromDb.get(1)) {
            id == auction2.id
            loanAmount == auction2.loanAmount
            timePeriod == auction2.timePeriod
            interestRate == auction2.interestRate
            endDate == auction2.endDate
            userId == auction2.userId
            auctionStatus == auction2.auctionStatus
            creationTime != null
        }
    }

    def 'Should return list of all auctions by auction status and without user auctions'() {
        given:
        UUID userUUID = addUserToDb()
        UUID secondUserUUID = addSecondUserToDb()
        addAuctionToDb(userUUID, AuctionStatus.ACTIVE)
        Auction auction = addAuctionToDb(secondUserUUID, AuctionStatus.ACTIVE)
        addAuctionToDb(userUUID, AuctionStatus.TERMINATED)


        when:
        def fromDb = repository.findAllByUserIdIsNotAndAuctionStatus(userUUID, AuctionStatus.ACTIVE)

        then:
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == auction.id
            loanAmount == auction.loanAmount
            timePeriod == auction.timePeriod
            interestRate == auction.interestRate
            endDate == auction.endDate
            userId == auction.userId
            auctionStatus == auction.auctionStatus
            creationTime != null
        }
    }

    def 'Should return list of all auctions by user id and auction status'() {
        given:
        UUID userUUID = addUserToDb()
        UUID secondUserUUID = addSecondUserToDb()
        Auction auction = addAuctionToDb(userUUID, AuctionStatus.ACTIVE)
        addAuctionToDb(secondUserUUID, AuctionStatus.ACTIVE)
        addAuctionToDb(userUUID, AuctionStatus.TERMINATED)

        when:
        def fromDb = repository.findAllByUserIdAndAuctionStatus(userUUID, AuctionStatus.ACTIVE)

        then:
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == auction.id
            loanAmount == auction.loanAmount
            timePeriod == auction.timePeriod
            interestRate == auction.interestRate
            endDate == auction.endDate
            userId == auction.userId
            auctionStatus == AuctionStatus.ACTIVE
            creationTime != null
        }
    }

    def 'Should return list of all auctions by auction status' () {
        given:
        UUID userUUID = addUserToDb()
        Auction auction = addAuctionToDb(userUUID, AuctionStatus.ACTIVE)
        addAuctionToDb(userUUID, AuctionStatus.TERMINATED)

        when:
        def fromDb = repository.findAllByAuctionStatus(AuctionStatus.ACTIVE)

        then:
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == auction.id
            loanAmount == auction.loanAmount
            timePeriod == auction.timePeriod
            interestRate == auction.interestRate
            endDate == auction.endDate
            userId == auction.userId
            auctionStatus == AuctionStatus.ACTIVE
            creationTime != null
        }
    }

    def 'Should return auction by id and auction status' () {
        given:
        UUID userUUID = addUserToDb()
        Auction auction = addAuctionToDb(userUUID, AuctionStatus.ACTIVE)

        when:
        def fromDb = repository.findByIdAndAuctionStatus(auction.id, AuctionStatus.ACTIVE)

        then:
        fromDb.get() == auction
    }

    def "Should delete auction by id"() {
        given:
        UUID userUUID = addUserToDb()
        Auction auction = addAuctionToDb(userUUID, AuctionStatus.ACTIVE)

        when:
        repository.deleteById(auction.id)
        def foundAuction = repository.findById(auction.id)

        then:
        foundAuction.empty
    }

    UUID addUserToDb() {
        return userService.register(UserData.USER_REGISTER_REQUEST)
    }

    UUID addSecondUserToDb() {
        return userService.register(new UserRegisterRequest("email@email.pl", "string", "string", "string", "string"))
    }

    Auction addAuctionToDb(UUID userUUID, AuctionStatus auctionStatus){
        repository.save(new Auction(UUID.randomUUID(), BigDecimal.valueOf(RandomUtils.nextInt(0, 10000)), RandomUtils.nextInt(1,36),
                RandomUtils.nextFloat(0,20), AuctionDataFictureFactory.DATE, userUUID, auctionStatus, LocalDateTime.now()))
    }
}
