package pl.fintech.dragons.dragonslending;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Subject


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OfferRepositoryIT extends PostgreSQLContainerSpe                {

    @Subject
    @Autowired
    OfferRepository repository

    def cleanup() {
        repository.deleteAll()
    }

    def 'Should store new offer'() {
        given:
        def offer = new Offer()

        when:
        repository.save(offer)

        then:
        def fromDb = repository.findAll()
        fromDb.size() == 1

        and:
        with(fromDb.first()) {
            id == basket.id
            value == basket.value
            currency == basket.currency
            riskType == basket.riskType
        }
    }
}
