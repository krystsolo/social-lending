package pl.fintech.dragons.dragonslending.sociallending.offer

import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.fintech.dragons.dragonslending.PostgreSQLContainerSpecification
import pl.fintech.dragons.dragonslending.sociallending.auction.Auction
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionDataFictureFactory
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionRepository
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionStatus
import pl.fintech.dragons.dragonslending.sociallending.identity.UserData
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import pl.fintech.dragons.dragonslending.sociallending.identity.application.web.UserRegisterRequest
import pl.fintech.dragons.dragonslending.sociallending.identity.infrastructure.UserConfig
import pl.fintech.dragons.dragonslending.sociallending.offer.config.OfferConfig
import spock.lang.Subject

@DataJpaTest
@ActiveProfiles("integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [OfferConfig.class, UserConfig.class])
class OfferRepositoryIT extends PostgreSQLContainerSpecification {

    @Subject
    @Autowired
    OfferRepository repository

    @Autowired
    UserService userService

    @Autowired
    AuctionRepository auctionRepository

    def cleanup() {
        repository.deleteAll()
    }

    def 'Should store new offer'() {
        given:
        UUID userUUID = addUserToDb()
        UUID auctionUUID = addAuctionToDb(userUUID)
        Offer offer = addOfferToDb(userUUID, auctionUUID, OfferStatus.ACTIVE)

        when:
        repository.save(offer)

        then:
        def fromDb = repository.findAll()
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == offer.id
            offerAmount == offer.offerAmount
            timePeriod == offer.timePeriod
            interestRate == offer.interestRate
            auctionId == offer.auctionId
            userId == offer.userId
        }
    }

    def 'Should return list of all offers by user id and offer status'() {
        given:
        UUID userUUID = addUserToDb()
        UUID userUUID2 = addSecondUserToDb()
        UUID auctionUUID = addAuctionToDb(userUUID)
        Offer offer1 = addOfferToDb(userUUID, auctionUUID, OfferStatus.ACTIVE)
        addOfferToDb(userUUID2, auctionUUID, OfferStatus.ACTIVE)
        addOfferToDb(userUUID, auctionUUID, OfferStatus.TERMINATED)

        when:
        def fromDb = repository.findAllByUserIdAndOfferStatus(userUUID, OfferStatus.ACTIVE)

        then:
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == offer1.id
            offerAmount == offer1.offerAmount
            timePeriod == offer1.timePeriod
            interestRate == offer1.interestRate
            auctionId == offer1.auctionId
            userId == offer1.userId
        }
    }

    def 'Should return list of all offers by auction id'() {
        given:
        UUID userUUID = addUserToDb()
        UUID auctionUUID = addAuctionToDb(userUUID)
        UUID auctionUUID2 = addAuctionToDb(userUUID)
        Offer offer = addOfferToDb(userUUID, auctionUUID, OfferStatus.ACTIVE)
        addOfferToDb(userUUID, auctionUUID2, OfferStatus.ACTIVE)

        when:
        def fromDb = repository.findAllByAuctionId(auctionUUID)

        then:
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == offer.id
            offerAmount == offer.offerAmount
            timePeriod == offer.timePeriod
            interestRate == offer.interestRate
            auctionId == offer.auctionId
            userId == offer.userId
        }
    }

    def 'Should find offer by auction id and user id' () {
        given:
        UUID userUUID = addUserToDb()
        UUID auctionUUID = addAuctionToDb(userUUID)
        Offer offer = addOfferToDb(userUUID, auctionUUID, OfferStatus.ACTIVE)

        when:
        def fromDb = repository.findByAuctionIdAndUserId(auctionUUID, userUUID)

        then:
        fromDb.get() == offer
    }

    def "Should delete auction by id"() {
        given:
        UUID userUUID = addUserToDb()
        UUID auctionUUID = addAuctionToDb(userUUID)
        Offer offer = addOfferToDb(userUUID, auctionUUID, OfferStatus.ACTIVE)

        when:
        repository.deleteById(offer.id)
        def foundOffer = repository.findById(offer.id)

        then:
        foundOffer.empty
    }

    UUID addUserToDb() {
        return userService.register(UserData.USER_REGISTER_REQUEST)
    }

    UUID addSecondUserToDb() {
        return userService.register(new UserRegisterRequest("email@email.pl", "string", "string", "string", "string"))
    }

    UUID addAuctionToDb(UUID userUUID) {
        return auctionRepository.save(new Auction(BigDecimal.valueOf(RandomUtils.nextInt(0, 10000)), RandomUtils.nextInt(1, 36), RandomUtils.nextFloat(0, 20), AuctionDataFictureFactory.DATE, userUUID)).id
    }

    Offer addOfferToDb(UUID userUUID, UUID auctionUUID, OfferStatus offerStatus) {
        repository.save(new Offer(UUID.randomUUID(), BigDecimal.valueOf(1000), 4.0, RandomUtils.nextInt(1, 36), auctionUUID, userUUID, offerStatus))
    }
}
