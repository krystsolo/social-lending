package pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.initializer

import org.springframework.http.ResponseEntity
import spock.lang.Specification

class IdentityLocationHeaderRetrieverTest extends Specification {

    def "should retrieve id from response location header"() {
        given:
        UUID uuid = UUID.randomUUID()
        def responseEntity = ResponseEntity.created(new URI('https://localhost.pl/api/created/' + uuid)).build()
        def retriever = new IdentityLocationHeaderRetriever()

        when:
        def retrievedId = retriever.retrieve(responseEntity)

        then:
        retrievedId == uuid
    }
}
