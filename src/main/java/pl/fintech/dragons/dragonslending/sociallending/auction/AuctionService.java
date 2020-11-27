package pl.fintech.dragons.dragonslending.sociallending.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto;
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionRequest;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
public class AuctionService {
  private final AuctionRepository auctionRepository;
  private final UserService userService;
  private final EventPublisher eventPublisher;

  @Transactional(readOnly = true)
  public AuctionQueryDto getAuction(UUID id) {
    Optional<Auction> auction = auctionRepository.findByIdAndAuctionStatus(id, AuctionStatus.ACTIVE);

    if (auction.isPresent()) {
      UserDto user = userService.getUser(auction.get().getUserId());
      return auction.get().toAuctionDto(
          user.getUsername()
      );
    } else {
      throw new IllegalStateException("An auction could not be found");
    }
  }

  @Transactional(readOnly = true)
  public List<AuctionQueryDto> getAllNotCurrentUserAuctions() {
    return mapAuctionListToDto(auctionRepository
        .findAllByUserIdIsNotAndAuctionStatus(
            userService.getCurrentLoggedUser().getId(),
            AuctionStatus.ACTIVE));
  }

  @Transactional(readOnly = true)
  public List<AuctionQueryDto> getCurrentUserAuctions() {
    return mapAuctionListToDto(auctionRepository
        .findAllByUserIdAndAuctionStatus(
            userService.getCurrentLoggedUser().getId(),
            AuctionStatus.ACTIVE));
  }

  @Transactional(readOnly = true)
  public List<AuctionQueryDto> getPublicAuctions() {
    return mapAuctionListToDto(auctionRepository
        .findAllByAuctionStatus(AuctionStatus.ACTIVE));
  }

  public UUID saveAuction(AuctionRequest dto) {
    Auction def = new Auction(
        dto.getLoanAmount(),
        dto.getTimePeriod(),
        dto.getInterestRate(),
        dto.getEndDate(),
        userService.getCurrentLoggedUser().getId()
    );

    auctionRepository.save(def);
    return def.getId();
  }

  public UUID updateAuction(AuctionRequest dto, UUID id) throws AccessDeniedException {
    Auction auction = auctionRepository.getOne(id);

    if (!userService.getCurrentLoggedUser().getId().equals(auction.getUserId())) {
      throw new AccessDeniedException("You don't have permission to update this auction");
    }

    auction.changeAuctionParameters(
        dto.getLoanAmount(),
        dto.getTimePeriod(),
        dto.getInterestRate(),
        dto.getEndDate()
    );

    return auction.getId();
  }

  public void deleteAuction(UUID auctionId) throws AccessDeniedException {
    UserDto user = userService.getCurrentLoggedUser();
    Auction auction = auctionRepository.getOne(auctionId);

    if (!user.getId().equals(auction.getUserId())) {
      throw new AccessDeniedException("You don't have permission to delete this auction");
    }

    eventPublisher.publish(AuctionTerminated.now(user.getId(), auctionId));
    auction.makeAuctionTerminated();
    auctionRepository.save(auction);
  }

  private List<AuctionQueryDto> mapAuctionListToDto(List<Auction> auctions) {
    return auctions
        .stream()
        .map(e -> e.toAuctionDto(
            userService.getUser(e.getUserId()).getUsername())
        )
        .collect(Collectors.toList());
  }
}
