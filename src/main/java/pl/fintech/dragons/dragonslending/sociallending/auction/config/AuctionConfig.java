package pl.fintech.dragons.dragonslending.sociallending.auction.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.sociallending.auction.Auction;
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionRepository;
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionService;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;

@Configuration
@EnableJpaRepositories(basePackageClasses = AuctionRepository.class)
@EntityScan(basePackageClasses = Auction.class)
public class AuctionConfig {

  @Bean
  AuctionService auctionService(AuctionRepository auctionRepository, UserService userService) {
    return new AuctionService(auctionRepository, userService);
  }
}
