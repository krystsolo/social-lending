package pl.fintech.dragons.dragonslending.sociallending.auction.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.common.events.publisher.EventPublisherConfig;
import pl.fintech.dragons.dragonslending.sociallending.auction.*;
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.Auction;
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionRepository;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.sociallending.identity.infrastructure.UserConfig;

@Configuration
@EnableJpaRepositories(basePackageClasses = AuctionRepository.class)
@EntityScan(basePackageClasses = Auction.class)
@Import({UserConfig.class, EventPublisherConfig.class})
public class AuctionConfig {

  @Bean
  AuctionService auctionService(AuctionRepository auctionRepository, UserService userService, EventPublisher eventPublisher) {
    return new AuctionService(auctionRepository, userService, eventPublisher);
  }

  @Bean
  AuctionListener auctionListener(AuctionRepository auctionRepository) {
    return new AuctionListener(auctionRepository);
  }

  @Bean
  AuctionTerminator auctionTerminator(AuctionRepository auctionRepository, EventPublisher eventPublisher) {
    return new AuctionTerminator(auctionRepository, eventPublisher);
  }
}
