package pl.fintech.dragons.dragonslending.offer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pl.fintech.dragons.dragonslending.PostgreSQLContainerSpecification
import pl.fintech.dragons.dragonslending.identity.UserData
import pl.fintech.dragons.dragonslending.identity.application.UserService
import pl.fintech.dragons.dragonslending.identity.infrastructure.UserConfig
import pl.fintech.dragons.dragonslending.offer.Offer
import pl.fintech.dragons.dragonslending.offer.OfferRepository
import pl.fintech.dragons.dragonslending.offer.config.OfferConfig
import spock.lang.Subject

import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [OfferConfig.class, UserConfig.class])
class OfferRepositoryIT extends PostgreSQLContainerSpecification {

    @Subject
    @Autowired
    OfferRepository repository

    @Autowired
    UserService userService

    def cleanup() {
        repository.deleteAll()
    }

    def 'Should store new offer'() {
        given:
        UUID userId = addUserToDb()
        def offer = new Offer(UUID.randomUUID(), BigDecimal.valueOf(1000), 5, 4.0, LocalDate.now(), userId)

        when:
        repository.save(offer)

        then:
        def fromDb = repository.findAll()
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == offer.id
        }
    }

    UUID addUserToDb() {
        return userService.register(UserData.USER_REGISTER_REQUEST)
    }
}
