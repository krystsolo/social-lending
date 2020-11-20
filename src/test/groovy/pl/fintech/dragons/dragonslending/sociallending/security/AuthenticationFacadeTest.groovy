package pl.fintech.dragons.dragonslending.sociallending.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import pl.fintech.dragons.dragonslending.sociallending.identity.domain.UserPrincipal
import spock.lang.Specification

class AuthenticationFacadeTest extends Specification {

    AuthenticationFromSecurityContextRetriever authenticationFacade = new AuthenticationFromSecurityContextRetriever()

    def "should return id of current logged user"() {
        given:
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext()
        UserPrincipal userPrincipal = new UserPrincipal(UUID.randomUUID(), "email", "password")
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, "password"))
        SecurityContextHolder.setContext(securityContext)

        when:
        UUID id = authenticationFacade.idOfCurrentLoggedUser()

        then:
        id == userPrincipal.id

    }

    def "should return email of current logged user"() {
        given:
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext()
        UserPrincipal userPrincipal = new UserPrincipal(UUID.randomUUID(), "email", "password")
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userPrincipal, "password"))
        SecurityContextHolder.setContext(securityContext)

        when:
        String email = authenticationFacade.emailOfCurrentLoggedUser()

        then:
        email == userPrincipal.username
    }
}
