package pl.fintech.dragons.dragonslending.sociallending.lending.loan.infrastructure

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
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.Loan
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculation
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanFixtures
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanRepository
import spock.lang.Subject

@DataJpaTest
@ActiveProfiles("integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [LoanConfig.class, UserConfig.class])
class LoanRepositoryAdapterIT extends PostgreSQLContainerSpecification {

    @Subject
    @Autowired
    LoanRepository repository

    @Autowired
    UserService userService

    def cleanup() {
        repository.deleteAll()
    }

    def 'Should store new loan'() {
        given:
        UUID borrowerId = addBorrowerToDb()
        UUID lenderId = addLenderToDb()
        def loan = Loan.from(borrowerId, lenderId, new LoanCalculation(BigDecimal.TEN, BigDecimal.TEN), 10)

        when:
        repository.save(loan)

        then:
        def fromDb = repository.findAll()
        fromDb.size() == 1
    }

    def 'Should return auction by id'() {
        given:
        UUID borrowerId = addBorrowerToDb()
        UUID lenderId = addLenderToDb()
        def loan = Loan.from(borrowerId, lenderId, new LoanCalculation(BigDecimal.TEN, BigDecimal.TEN), 10)

        when:
        def fromDb = repository.getOne(loan.id)

        then:
        with(fromDb) {
            id == loan.id
        }
    }

    def 'Should return list of auctions by user id (taken and granted)'() {
        given:
        UUID borrowerId = addBorrowerToDb()
        UUID lenderId = addLenderToDb()
        def loan1 = Loan.from(borrowerId, lenderId, new LoanCalculation(BigDecimal.TEN, BigDecimal.TEN), 10)
        def loan2 = Loan.from(lenderId, borrowerId, new LoanCalculation(BigDecimal.TEN, BigDecimal.TEN), 10)
        repository.save(loan1)
        repository.save(loan2)

        when:
        def fromDb = repository.findAll()

        then:
        fromDb.size() == 2
    }

    def 'Should return list of auctions with today as next installment day'() {
        given:
        UUID borrowerId = addBorrowerToDb()
        UUID lenderId = addLenderToDb()
        def loanWithTodayAsNextInstallment = LoanFixtures.loanWithTodayAsNextInstallment(borrowerId, lenderId)
        repository.save(loanWithTodayAsNextInstallment)
        def loanWithNotTodayAsInstallmentDay = LoanFixtures.loanWithNotTodayAsInstallmentDay(borrowerId, lenderId)
        repository.save(loanWithNotTodayAsInstallmentDay)

        when:
        def fromDb = repository.findAllLoansWithTodayAsNextInstallmentDay()

        then:
        fromDb.size() == 1
        and:
        with(fromDb.first()) {
            id == loanWithTodayAsNextInstallment.id
        }
    }

    def 'Should return list of auctions with unpaid installments'() {
        given:
        UUID borrowerId = addBorrowerToDb()
        UUID lenderId = addLenderToDb()
        def loanWithUnpaidInstallment = LoanFixtures.loanWithUnpaidInstallmentFor(borrowerId, lenderId)
        repository.save(loanWithUnpaidInstallment)
        def loanWithPaidInstallment = LoanFixtures.loanWithPaidInstallmentFor(borrowerId, lenderId)
        repository.save(loanWithPaidInstallment)

        when:
        def fromDb = repository.findAllLoansWithUnpaidInstallments()

        then:
        fromDb.size() == 1
        and:
        with(fromDb.first()) {
            id == loanWithUnpaidInstallment.id
        }
    }


    UUID addBorrowerToDb() {
        return userService.register(UserData.USER_REGISTER_REQUEST)
    }

    UUID addLenderToDb() {
        return userService.register(new UserRegisterRequest("email@email.pl", "string", "string", "string", "string"))
    }
}