package pl.fintech.dragons.dragonslending.paymentplatformmock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.common.events.publisher.EventPublisherConfig;
import pl.fintech.dragons.dragonslending.paymentplatformmock.client.BankClient;
import pl.fintech.dragons.dragonslending.paymentplatformmock.client.BankClientConfig;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.AccountFinder;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.AccountConfig;
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationConfig;
import pl.fintech.dragons.dragonslending.sociallending.security.AuthenticationFacade;

@Configuration
@Import({BankClientConfig.class, EventPublisherConfig.class, AccountConfig.class, AuthenticationConfig.class})
public class PaymentPlatformConfig {

    @Bean
    PaymentService paymentService(BankClient bankClient, EventPublisher eventPublisher,
                                  AccountFinder accountFinder, AuthenticationFacade authenticationFacade) {
        return new PaymentService(bankClient, eventPublisher, accountFinder, authenticationFacade);
    }
}
