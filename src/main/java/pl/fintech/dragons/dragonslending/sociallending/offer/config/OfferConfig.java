package pl.fintech.dragons.dragonslending.sociallending.offer.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.common.events.publisher.EventPublisherConfig;
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionService;
import pl.fintech.dragons.dragonslending.sociallending.auction.config.AuctionConfig;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.LoanCalculationService;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.infrastructure.LoanConfig;
import pl.fintech.dragons.dragonslending.sociallending.offer.Offer;
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferListener;
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferRepository;
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferService;

@Configuration
@EnableJpaRepositories(basePackageClasses = OfferRepository.class)
@EntityScan(basePackageClasses = Offer.class)
@Import({LoanConfig.class, AuctionConfig.class, EventPublisherConfig.class})
public class OfferConfig {

  @Bean
  OfferService offerService(OfferRepository offerRepository, LoanCalculationService loanCalculationService, UserService userService,
                            AuctionService auctionService, EventPublisher eventPublisher) {
    return new OfferService(offerRepository, loanCalculationService, userService, auctionService, eventPublisher);
  }

  @Bean
  OfferListener offerListener(OfferRepository offerRepository, EventPublisher eventPublisher) {
    return new OfferListener(offerRepository, eventPublisher);
  }
}
