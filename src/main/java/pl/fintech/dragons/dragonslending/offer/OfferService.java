package pl.fintech.dragons.dragonslending.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.identity.application.UserDto;
import pl.fintech.dragons.dragonslending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.offer.calculator.OfferCalculator;
import pl.fintech.dragons.dragonslending.offer.dto.CalculationDto;
import pl.fintech.dragons.dragonslending.offer.dto.OfferQueryDto;
import pl.fintech.dragons.dragonslending.offer.dto.OfferRequest;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
public class OfferService {
  private final OfferRepository offerRepository;
  private final OfferCalculator offerCalculator;
  private final UserService userService;

  @Transactional(readOnly = true)
  public OfferQueryDto getOffer(UUID id) {
    Offer offer = offerRepository.getOne(id);
    CalculationDto calculationDto = offerCalculator.calculate(offer);
    UserDto user = userService.getUser(offer.getUserId());
    return offer.toOfferQueryDto(
        calculationDto,
        user.getUsername()
    );
  }

  @Transactional(readOnly = true)
  public List<OfferQueryDto> getOffers() {
    return offerRepository
        .findAll()
        .stream()
        .map(e -> e.toOfferQueryDto(
            offerCalculator.calculate(e),
            userService.getUser(e.getUserId()).getUsername())
        )
        .collect(Collectors.toList());
  }

  UUID saveOfferDto(OfferRequest dto) {
    Offer def = new Offer(
        dto.getLoanAmount(),
        dto.getTimePeriod(),
        dto.getInterestRate(),
        dto.getEndDate(),
        userService.getCurrentLoggedUser().getId()
    );

    offerRepository.save(def);
    return def.getId();
  }

  UUID updateOfferDto(OfferRequest dto) throws AccessDeniedException {
    if (dto.getId() == null) {
      throw new IllegalArgumentException("Object cannot be updated, id is null");
    }

    Offer offer = offerRepository.getOne(dto.getId());

    if (!userService.getCurrentLoggedUser().getId().equals(offer.getUserId())) {
      throw new AccessDeniedException("You don't have permission to update this offer");
    }

    offer.changeOfferParameters(
        dto.getLoanAmount(),
        dto.getTimePeriod(),
        dto.getInterestRate(),
        dto.getEndDate()
    );

    offerRepository.save(offer);
    return offer.getId();
  }
}
