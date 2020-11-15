package pl.fintech.dragons.dragonslending.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.identity.application.UserDto;
import pl.fintech.dragons.dragonslending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.auction.calculator.AuctionCalculator;
import pl.fintech.dragons.dragonslending.auction.dto.CalculationDto;
import pl.fintech.dragons.dragonslending.auction.dto.AuctionQueryDto;
import pl.fintech.dragons.dragonslending.auction.dto.AuctionRequest;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
public class AuctionService {
  private final AuctionRepository auctionRepository;
  private final AuctionCalculator auctionCalculator;
  private final UserService userService;

  @Transactional(readOnly = true)
  public AuctionQueryDto getAuction(UUID id) {
    Auction auction = auctionRepository.getOne(id);
    CalculationDto calculationDto = auctionCalculator.calculate(auction);
    UserDto user = userService.getUser(auction.getUserId());
    return auction.toAuctionDto(
        calculationDto,
        user.getUsername()
    );
  }

  @Transactional(readOnly = true)
  public List<AuctionQueryDto> getAuctions() {
    return auctionRepository
        .findAll()
        .stream()
        .map(e -> e.toAuctionDto(
            auctionCalculator.calculate(e),
            userService.getUser(e.getUserId()).getUsername())
        )
        .collect(Collectors.toList());
  }

  public UUID saveAuctionDto(AuctionRequest dto) {
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

  public UUID updateAuctionDto(AuctionRequest dto) throws AccessDeniedException {
    if (dto.getId() == null) {
      throw new IllegalArgumentException("Object cannot be updated, id is null");
    }

    Auction auction = auctionRepository.getOne(dto.getId());

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
}
