package pl.fintech.dragons.dragonslending.offer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import pl.fintech.dragons.dragonslending.PostgreSQLContainerSpecification
import spock.lang.Subject

import java.time.LocalDate


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OfferRepositoryIT extends PostgreSQLContainerSpecification {

    @Subject
    @Autowired
    OfferRepository repository

    def cleanup() {
        repository.deleteAll()
    }

    def 'Should store new offer'() {
        given:
        def offer = new Offer(UUID.randomUUID(), BigDecimal.valueOf(1000), 5, 4.0, LocalDate.now(), "User")

        when:
        repository.save(offer)

        then:
        def fromDb = repository.findAll()
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == offer.id
            loanAmount == offer.loanAmount
            timePeriod == offer.timePeriod
            interestRate == offer.interestRate
            endDate == offer.endDate
        }
    }
}
