package pl.fintech.dragons.dragonslending.sociallending.offer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {
  List<Offer> findAllByUserIdAndOfferStatus(UUID userId, OfferStatus offerStatus);

  List<Offer> findAllByAuctionIdAndOfferStatus(UUID auctionId, OfferStatus offerStatus);

  Optional<Offer> findByAuctionIdAndUserIdAndOfferStatus(UUID auctionId, UUID userId, OfferStatus offerStatus);

  List<Offer> findAllByAuctionIdInAndOfferStatus(List<UUID> auctionList, OfferStatus offerStatus);
}
