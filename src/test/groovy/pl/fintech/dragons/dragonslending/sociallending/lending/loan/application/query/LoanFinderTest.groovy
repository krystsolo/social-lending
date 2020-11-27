package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query

import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.Loan
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanDataFixtures
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanRepository
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class LoanFinderTest extends Specification {

    private LoanRepository loanRepository = Mock(LoanRepository)
    private UserService userService = Mock(UserService)
    private LoanViewAssembler loanViewAssembler = Mock(LoanViewAssembler)
    private LoanFinder loanFinder = new LoanFinder(loanRepository, userService, loanViewAssembler)

    def "should return loans for current user"() {
        given:
        def userId = UUID.randomUUID()
        userService.getCurrentLoggedUser() >> new UserDto(userId, "email", "username", "name", "name")
        def loansView = new LoansView(Collections.emptyList(), Collections.emptyList())

        when:
        def returned = loanFinder.getLoansForCurrentUser()

        then:
        1 * loanRepository.getAllByUserId(userId) >> Collections.emptyList()
        1 * loanViewAssembler.loanViewFrom(Collections.emptyList(), userId) >> loansView
        returned == loansView
    }

    def "should return details of loan of specific id"() {
        given:
        def userId = UUID.randomUUID()
        def loanId = UUID.randomUUID()
        userService.getUser(userId) >> new UserDto(userId, "email", "username", "name", "name")
        userService.getCurrentLoggedUser() >> new UserDto(userId, "email", "username", "name", "name")
        def loansDetailsView = new LoanDetailsView(UUID.randomUUID(), "username", LoanDetailsView.LoanType.GRANTED, LocalDateTime.now(), LocalDate.now(), BigDecimal.ZERO, new ArrayList())

        when:
        def returned = loanFinder.getLoanDetailsFor(loanId)

        then:
        1 * loanRepository.getOne(loanId) >> LoanDataFixtures.loanWithUnpaidInstallmentFor(userId, userId)
        1 * loanViewAssembler.loanDetailsViewFrom(_ as Loan, userId) >> loansDetailsView
        returned == loansDetailsView
    }
}
