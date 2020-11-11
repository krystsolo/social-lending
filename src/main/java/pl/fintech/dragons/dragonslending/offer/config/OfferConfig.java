package pl.fintech.dragons.dragonslending.offer.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.offer.Offer;
import pl.fintech.dragons.dragonslending.offer.OfferRepository;
import pl.fintech.dragons.dragonslending.offer.OfferService;

@Configuration
@EnableJpaRepositories(basePackageClasses = OfferRepository.class)
@EntityScan(basePackageClasses = Offer.class)
public class OfferConfig {

  @Bean
  OfferService offerService(OfferRepository offerRepository) {
    return new OfferService(offerRepository);
  }
}
