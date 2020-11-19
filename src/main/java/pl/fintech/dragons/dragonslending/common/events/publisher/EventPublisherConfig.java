package pl.fintech.dragons.dragonslending.common.events.publisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;

@Configuration
public class EventPublisherConfig {

    @Bean
    EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new LoggingEventPublisher(new ForwardEventPublisher(applicationEventPublisher));
    }
}
