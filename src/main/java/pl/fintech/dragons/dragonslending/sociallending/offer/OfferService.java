package pl.fintech.dragons.dragonslending.sociallending.offer;


import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionService;
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.sociallending.loanCalculator.LoanCalculator;
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferQueryDto;
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferRequest;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
public class OfferService {
  private final OfferRepository offerRepository;
  private final LoanCalculator loanCalculator;
  private final UserService userService;
  private final AuctionService auctionService;

  public List<OfferQueryDto> getCurrentLoggedUserOffers() {
    return mapOfferListToDto(offerRepository
        .findAllByUserId(userService.getCurrentLoggedUser().getId()));
  }

  public List<OfferQueryDto> getOffersByAuctionId(UUID auctionId) {
    return mapOfferListToDto(offerRepository
        .findAllByAuctionId(auctionId));
  }

  public UUID saveOffer(OfferRequest dto) {
    AuctionQueryDto auction = auctionService.getAuction(dto.getAuctionId());

    if (auction == null) {
      throw new IllegalArgumentException("Incorrect auction id");
    }

    Offer def = new Offer(
        dto.getOfferAmount(),
        dto.getInterestRate(),
        auction.getTimePeriod(),
        dto.getAuctionId(),
        userService.getCurrentLoggedUser().getId()
    );

    offerRepository.save(def);
    return def.getId();
  }

  public void deleteOffer(UUID offerId) throws AccessDeniedException {
    if (userService.getCurrentLoggedUser().getId() != offerRepository.getOne(offerId).userId) {
      throw new AccessDeniedException("You don't have permission to delete this offer");
    }
    offerRepository.deleteById(offerId);
  }

  private List<OfferQueryDto> mapOfferListToDto(List<Offer> offers) {
    return offers
        .stream()
        .map(offer -> offer.toOfferDto(
            userService.getUser(offer.getUserId()).getUsername(),
            loanCalculator.calculate(offer.getOfferAmount(), offer.getTimePeriod(), offer.getInterestRate()))
        )
        .collect(Collectors.toList());
  }
}
