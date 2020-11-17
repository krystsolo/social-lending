package pl.fintech.dragons.dragonslending.payment.account

import pl.fintech.dragons.dragonslending.payment.account.domain.Account

class AccountFixtures {

    static final UUID USER_ID = UUID.randomUUID()

    static final UUID ACCOUNT_ID = UUID.randomUUID()

    static final BigDecimal BALANCE = BigDecimal.valueOf(1000)

    static final BigDecimal FROZEN_MONEY = BigDecimal.valueOf(200)

    static final Account ACCOUNT = new Account(ACCOUNT_ID, USER_ID, BALANCE, FROZEN_MONEY)
}
