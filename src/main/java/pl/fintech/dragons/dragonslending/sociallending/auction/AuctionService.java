package pl.fintech.dragons.dragonslending.sociallending.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
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

  @Transactional(readOnly = true)
  public AuctionQueryDto getAuction(UUID id) {
    Auction auction = auctionRepository.getOne(id);
    UserDto user = userService.getUser(auction.getUserId());
    return auction.toAuctionDto(
        user.getUsername()
    );
  }

  @Transactional(readOnly = true)
  public List<AuctionQueryDto> getAllNotCurrentUserAuctions() {
    return mapAuctionListToDto(auctionRepository
        .findAllByUserIdIsNot(userService.getCurrentLoggedUser().getId()));
  }

  @Transactional(readOnly = true)
  public List<AuctionQueryDto> getCurrentUserAuctions() {
    return mapAuctionListToDto(auctionRepository
        .findAllByUserId(userService.getCurrentLoggedUser().getId()));
  }

  @Transactional(readOnly = true)
  public List<AuctionQueryDto> getPublicAuctions() {
    return mapAuctionListToDto(auctionRepository
        .findAll());
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
    if (id == null) {
      throw new IllegalArgumentException("Object cannot be updated, id is null");
    }

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
    if (userService.getCurrentLoggedUser().getId() != auctionRepository.getOne(auctionId).userId) {
      throw new AccessDeniedException("You don't have permission to delete this auction");
    }
    auctionRepository.deleteById(auctionId);
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
