package pl.fintech.dragons.dragonslending.paymentplatformmock.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import pl.fintech.dragons.dragonslending.common.client.bankapi.BankClientConfiguration;

@Configuration
@EnableFeignClients(clients = {
        BankClient.class
})
@Import(BankClientConfiguration.class)
public class BankClientConfig {

    @Bean
    @Profile("!dev")
    public BankClientFacade bankClientFacade(BankClient bankClient) {
        return new BankClientFacade(bankClient);
    }

    @Bean
    @Profile("dev")
    public BankClientFacade bankClientFacadeMock() {
        return new BankClientFacade(new BankClientMock());
    }
}
