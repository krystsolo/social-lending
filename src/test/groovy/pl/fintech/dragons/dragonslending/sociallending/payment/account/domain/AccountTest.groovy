package pl.fintech.dragons.dragonslending.sociallending.payment.account.domain


import spock.lang.Specification

class AccountTest extends Specification {
    def "should withdraw money when there is enough balance"() {
        given:
        def account = new Account(UUID.randomUUID())
        account.deposit(BigDecimal.TEN)

        when:
        account.withdraw(BigDecimal.TEN)

        then:
        account.balance == 0
    }

    def "should not withdraw money when amount is bigger than balance"() {
        def account = new Account(UUID.randomUUID())

        when:
        account.withdraw(BigDecimal.TEN)

        then:
        thrown(IllegalStateException)
    }

    def "should validate that amount to withdraw is not negative number"() {
        given:
        def account = new Account(UUID.randomUUID())

        when:
        account.withdraw(BigDecimal.valueOf(-100))

        then:
        thrown(IllegalArgumentException)
    }

    def "should deposit amount of money"() {
        given:
        def account = new Account(UUID.randomUUID())

        when:
        account.deposit(BigDecimal.TEN)

        then:
        account.balance == BigDecimal.TEN
    }

    def "should validate that amount to deposit is not negative number"() {
        given:
        def account = new Account(UUID.randomUUID())

        when:
        account.deposit(BigDecimal.valueOf(-100))

        then:
        thrown(IllegalArgumentException)
    }

    def "should freeze money on account"() {
        given:
        def account = new Account(UUID.randomUUID())
        account.deposit(BigDecimal.TEN)

        when:
        account.freeze(BigDecimal.TEN)

        then:
        account.frozenAmount == BigDecimal.TEN
    }

    def "should not freeze money if there is no enough on account"() {
        given:
        def account = new Account(UUID.randomUUID())
        account.deposit(BigDecimal.TEN)

        when:
        account.freeze(BigDecimal.valueOf(10000))

        then:
        thrown(IllegalStateException)
    }

    def "should unfreeze money on account"() {
        given:
        def account = new Account(UUID.randomUUID())
        account.deposit(BigDecimal.TEN)
        account.freeze(BigDecimal.TEN)

        when:
        account.unfreeze(BigDecimal.TEN)

        then:
        account.frozenAmount == 0
    }

    def "should not unfreeze money on account when amount is bigger then frozen amount"() {
        given:
        def account = new Account(UUID.randomUUID())
        account.deposit(BigDecimal.TEN)
        account.freeze(BigDecimal.TEN)

        when:
        account.unfreeze(BigDecimal.valueOf(1000))

        then:
        thrown(IllegalStateException)
    }

    def "should unfreeze all money on account"() {
        given:
        def account = new Account(UUID.randomUUID())
        account.deposit(BigDecimal.TEN)
        account.freeze(BigDecimal.TEN)

        when:
        account.unfreezeAllMoney()

        then:
        account.availableBalance() == BigDecimal.TEN
    }

    def "should retrieve available balance on account"() {
        given:
        def account = new Account(UUID.randomUUID())
        account.deposit(BigDecimal.TEN)
        account.freeze(BigDecimal.valueOf(5))

        when:
        def availableBalance = account.availableBalance()

        then:
        availableBalance == 5
    }
}
