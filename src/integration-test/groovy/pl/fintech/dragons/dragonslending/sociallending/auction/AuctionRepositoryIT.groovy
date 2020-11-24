package pl.fintech.dragons.dragonslending.sociallending.auction

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.fintech.dragons.dragonslending.PostgreSQLContainerSpecification
import pl.fintech.dragons.dragonslending.sociallending.identity.UserData
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import pl.fintech.dragons.dragonslending.sociallending.identity.application.web.UserRegisterRequest
import pl.fintech.dragons.dragonslending.sociallending.identity.infrastructure.UserConfig
import pl.fintech.dragons.dragonslending.sociallending.auction.config.AuctionConfig
import spock.lang.Subject

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
        def auction = new Auction(BigDecimal.valueOf(1000), 5, 4.0, AuctionDataFictureFactory.DATE, userUUID)

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
        }
    }

    def 'Should return auction by id'() {
        given:
        UUID userUUID = addUserToDb()
        Auction auction = repository.save(
                new Auction(BigDecimal.valueOf(1000), 5, 4.0, AuctionDataFictureFactory.DATE, userUUID))

        when:
        def fromDb = repository.getOne(auction.id)

        then:
        with(fromDb) {
            id == auction.id
            loanAmount == auction.loanAmount
            timePeriod == auction.timePeriod
            interestRate == auction.interestRate
            endDate == auction.endDate
            userId == auction.userId
        }
    }

    def 'Should return list of all auctions'() {
        given:
        UUID userUUID = addUserToDb()
        Auction auction1 = repository.save(
                new Auction(BigDecimal.valueOf(1000), 5, 4.0, AuctionDataFictureFactory.DATE, userUUID))
        Auction auction2 = repository.save(
                new Auction(BigDecimal.valueOf(2000), 2, 2, AuctionDataFictureFactory.DATE, userUUID)
        )


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
        }

        and:
        with(fromDb.get(1)) {
            id == auction2.id
            loanAmount == auction2.loanAmount
            timePeriod == auction2.timePeriod
            interestRate == auction2.interestRate
            endDate == auction2.endDate
            userId == auction2.userId
        }
    }

    def 'Should return list of all auctions by user id'() {
        given:
        UUID userUUID = addUserToDb()
        UUID secondUserUUID = addSecondUserToDb()
        Auction auction1 = repository.save(
                new Auction(BigDecimal.valueOf(1000), 5, 4.0, AuctionDataFictureFactory.DATE, userUUID))
        repository.save(
                new Auction(BigDecimal.valueOf(2000), 2, 2, AuctionDataFictureFactory.DATE, secondUserUUID))


        when:
        def fromDb = repository.findAllByUserId(userUUID)

        then:
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == auction1.id
            loanAmount == auction1.loanAmount
            timePeriod == auction1.timePeriod
            interestRate == auction1.interestRate
            endDate == auction1.endDate
            userId == auction1.userId
        }
    }

    def 'Should return list of all auctions without auction with user id'() {
        given:
        UUID userUUID = addUserToDb()
        UUID secondUserUUID = addSecondUserToDb()
        Auction auction1 = repository.save(
                new Auction(BigDecimal.valueOf(1000), 5, 4.0, AuctionDataFictureFactory.DATE, userUUID))
        Auction auction2 = repository.save(
                new Auction(BigDecimal.valueOf(2000), 2, 2, AuctionDataFictureFactory.DATE, secondUserUUID)
        )


        when:
        def fromDb = repository.findAllByUserIdIsNot(userUUID)

        then:
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == auction2.id
            loanAmount == auction2.loanAmount
            timePeriod == auction2.timePeriod
            interestRate == auction2.interestRate
            endDate == auction2.endDate
            userId == auction2.userId
        }
    }

    def "Should delete auction by id"() {
        given:
        UUID userUUID = addUserToDb()
        Auction auction = repository.save(
                new Auction(BigDecimal.valueOf(1000), 5, 4.0, AuctionDataFictureFactory.DATE, userUUID))

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
}
