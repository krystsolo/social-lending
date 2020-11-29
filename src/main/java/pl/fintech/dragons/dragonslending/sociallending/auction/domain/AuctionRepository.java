package pl.fintech.dragons.dragonslending.sociallending.auction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID> {
  List<Auction> findAllByUserIdIsNotAndAuctionStatus(UUID userId, AuctionStatus auctionStatus);

  List<Auction> findAllByUserIdAndAuctionStatus(UUID userId, AuctionStatus auctionStatus);

  List<Auction> findAllByAuctionStatus(AuctionStatus auctionStatus);

  Optional<Auction> findByIdAndAuctionStatus(UUID auctionId, AuctionStatus auctionStatus);

  List<Auction> findAllByAuctionStatusAndEndDateBefore(AuctionStatus auctionStatus, LocalDate endDate);
}
