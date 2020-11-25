package pl.fintech.dragons.dragonslending.sociallending.offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {
  List<Offer> findAllByUserIdAndOfferStatus(UUID userId, OfferStatus offerStatus);

  List<Offer> findAllByAuctionId(UUID auctionId);
}
