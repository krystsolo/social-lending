package pl.fintech.dragons.dragonslending.paymentplatformmock.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.fintech.dragons.dragonslending.common.client.bankapi.BankClientConfiguration;

@Configuration
@EnableFeignClients(clients = {
        BankClient.class
})
@Import(BankClientConfiguration.class)
public class BankClientConfig {
}
