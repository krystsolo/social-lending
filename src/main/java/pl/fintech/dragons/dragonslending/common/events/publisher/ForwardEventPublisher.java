package pl.fintech.dragons.dragonslending.common.events.publisher;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;

@AllArgsConstructor
class ForwardEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
