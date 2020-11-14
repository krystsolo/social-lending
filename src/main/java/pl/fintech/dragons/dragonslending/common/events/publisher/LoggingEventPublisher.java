package pl.fintech.dragons.dragonslending.common.events.publisher;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.fintech.dragons.dragonslending.common.events.DomainEvent;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;

@AllArgsConstructor
@Slf4j
class LoggingEventPublisher implements EventPublisher {

    private final EventPublisher eventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.debug("Event published: {}", event);
        eventPublisher.publish(event);
    }
}
