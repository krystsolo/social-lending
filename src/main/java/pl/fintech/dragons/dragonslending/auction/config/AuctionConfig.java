package pl.fintech.dragons.dragonslending.auction.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.auction.Auction;
import pl.fintech.dragons.dragonslending.auction.AuctionRepository;
import pl.fintech.dragons.dragonslending.auction.AuctionService;
import pl.fintech.dragons.dragonslending.auction.calculator.AuctionCalculator;

@Configuration
@EnableJpaRepositories(basePackageClasses = AuctionRepository.class)
@EntityScan(basePackageClasses = Auction.class)
public class AuctionConfig {

  @Bean
  AuctionService auctionService(AuctionRepository auctionRepository, UserService userService) {
    return new AuctionService(auctionRepository, new AuctionCalculator(), userService);
  }
}
