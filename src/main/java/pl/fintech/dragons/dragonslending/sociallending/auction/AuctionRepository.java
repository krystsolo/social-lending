package pl.fintech.dragons.dragonslending.sociallending.auction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID> {
  List<Auction> findAllByUserIdIsNotAndAuctionStatus(UUID userId, AuctionStatus auctionStatus);

  List<Auction> findAllByUserIdAndAuctionStatus(UUID userId, AuctionStatus auctionStatus);

  List<Auction> findAllByAuctionStatus(AuctionStatus auctionStatus);

  Optional<Auction> findByIdAndAuctionStatus(UUID auctionId, AuctionStatus auctionStatus);
}
