package pl.fintech.dragons.dragonslending.sociallending.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.common.exceptions.ResourceNotFoundException;
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.Auction;
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionRepository;
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionStatus;
import pl.fintech.dragons.dragonslending.sociallending.auction.domain.AuctionTerminated;
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto;
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionRequest;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;

import java.nio.file.AccessDeniedException;
import java.util.List;
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
    Auction auction = auctionRepository.findByIdAndAuctionStatus(id, AuctionStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("An auction could not be found"));

    UserDto user = userService.getUser(auction.getUserId());
    return auction.toAuctionDto(
        user.getUsername()
    );
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
