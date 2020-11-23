package pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.bankapi;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import pl.fintech.dragons.dragonslending.common.client.bankapi.BankClientConfiguration;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.BankApiService;

@Configuration
@EnableFeignClients(clients = {
        BankApi.class
})
@Import(BankClientConfiguration.class)
public class BankApiConfig {

    @Bean
    @Profile("!dev")
    public BankApiService bankApiService(BankApi bankApi) {
        return new BankApiServiceAdapter(bankApi);
    }

    @Bean
    @Profile("dev")
    public BankApiService bankApiServiceMock() {
        return new BankApiServiceMock();
    }
}
