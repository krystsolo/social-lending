package pl.fintech.dragons.dragonslending.sociallending.offer.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionService;
import pl.fintech.dragons.dragonslending.sociallending.auction.config.AuctionConfig;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.sociallending.loanCalculator.LoanCalculator;
import pl.fintech.dragons.dragonslending.sociallending.loanCalculator.LoanCalculatorConfig;
import pl.fintech.dragons.dragonslending.sociallending.offer.Offer;
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferRepository;
import pl.fintech.dragons.dragonslending.sociallending.offer.OfferService;

@Configuration
@EnableJpaRepositories(basePackageClasses = OfferRepository.class)
@EntityScan(basePackageClasses = Offer.class)
@Import({LoanCalculatorConfig.class, AuctionConfig.class})
public class OfferConfig {

  @Bean
  OfferService offerService(OfferRepository offerRepository, LoanCalculator loanCalculator, UserService userService, AuctionService auctionService) {
    return new OfferService(offerRepository, loanCalculator, userService, auctionService);
  }
}
