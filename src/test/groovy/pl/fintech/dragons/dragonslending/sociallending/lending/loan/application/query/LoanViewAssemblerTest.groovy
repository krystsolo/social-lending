package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query

import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.Loan
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanDataFixtures
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanInstallmentQuery
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class LoanViewAssemblerTest extends Specification {

    UserService userService = Mock(UserService)
    LoanViewAssembler loanViewAssembler = new LoanViewAssembler(userService)

    def "should assembly loan view dto"() {
        given:
        UUID currentUserId = UUID.randomUUID()
        UUID anotherUserId = UUID.randomUUID()
        Loan loanTaken = LoanDataFixtures.loanWithPaidInstallmentFor(currentUserId, anotherUserId)
        Loan loanGranted = LoanDataFixtures.loanWithPaidInstallmentFor(anotherUserId, currentUserId)
        def user = new UserDto(anotherUserId, "email", "useername", "name", "name")
        userService.getUser(anotherUserId) >> user

        when:
        def loanView = loanViewAssembler.loanViewFrom([loanGranted, loanTaken], currentUserId)

        then:
        loanView.loansGranted.size() == 1
        loanView.loansTaken.size() == 1
        and:
        with(loanView.loansTaken.first()) {
            id == loanTaken.id
            username == user.username
            amount == loanTaken.amountToRepaidToLender + loanTaken.systemFee
            creationTime == loanTaken.creationTime
            nextInstallmentDate == loanTaken.nextInstallmentDate
            installmentsNumber == loanTaken.installmentsNumber
        }
        and:
        with(loanView.loansGranted.first()) {
            id == loanGranted.id
            username == user.username
            amount == loanGranted.amountToRepaidToLender
            creationTime == loanGranted.creationTime
            nextInstallmentDate == loanGranted.nextInstallmentDate
            installmentsNumber == loanGranted.installmentsNumber
        }
    }

    def "should assembly taken loan details view dto for finished loan"() {
        given:
        UUID currentUserId = UUID.randomUUID()
        UUID anotherUserId = UUID.randomUUID()
        Loan finishedLoan = LoanDataFixtures.finishedLoanFor(currentUserId, anotherUserId)
        def user = new UserDto(anotherUserId, "email", "useername", "name", "name")
        userService.getUser(anotherUserId) >> user

        when:
        def detailsView = loanViewAssembler.loanDetailsViewFrom(finishedLoan, currentUserId)

        then:
        with(detailsView) {
            id == finishedLoan.id
            username == user.username
            type == LoanDetailsView.LoanType.TAKEN
            creationTime == finishedLoan.creationTime
            nextInstallmentDate == finishedLoan.nextInstallmentDate
            calculatedRepaymentAmount == finishedLoan.amountToRepaidToLender + finishedLoan.systemFee
            loanInstallments.size() == 1
            loanInstallments == finishedLoan.getInstallments()
        }
    }

    def "should assembly granted loan details view dto for finished loan"() {
        given:
        UUID currentUserId = UUID.randomUUID()
        UUID anotherUserId = UUID.randomUUID()
        Loan finishedLoan = LoanDataFixtures.finishedLoanFor(anotherUserId, currentUserId)
        def user = new UserDto(anotherUserId, "email", "useername", "name", "name")
        userService.getUser(anotherUserId) >> user

        when:
        def detailsView = loanViewAssembler.loanDetailsViewFrom(finishedLoan, currentUserId)

        then:
        with(detailsView) {
            id == finishedLoan.id
            username == user.username
            type == LoanDetailsView.LoanType.GRANTED
            creationTime == finishedLoan.creationTime
            nextInstallmentDate == finishedLoan.nextInstallmentDate
            calculatedRepaymentAmount == finishedLoan.amountToRepaidToLender + finishedLoan.systemFee
            loanInstallments.size() == 1
            loanInstallments == finishedLoan.getInstallments()
        }
    }


    def "should assembly loan details view dto adding future installments for active loan"() {
        given:
        UUID currentUserId = UUID.randomUUID()
        UUID anotherUserId = UUID.randomUUID()
        Loan finishedLoan = LoanDataFixtures.loanWithPaidInstallmentFor(currentUserId, anotherUserId)
        def user = new UserDto(anotherUserId, "email", "useername", "name", "name")
        userService.getUser(anotherUserId) >> user

        when:
        def detailsView = loanViewAssembler.loanDetailsViewFrom(finishedLoan, currentUserId)

        then:
        with(detailsView) {
            loanInstallments.size() == finishedLoan.installmentsNumber
        }
    }
}
