package pl.fintech.dragons.dragonslending.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.fintech.dragons.dragonslending.offer.dto.OfferDto;
import pl.fintech.dragons.dragonslending.offer.dto.OfferReturnDto;
import pl.fintech.dragons.dragonslending.offer.dto.mapper.OfferMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferService {
  private final OfferRepository offerRepository;

  OfferDto getOffer(UUID id) {
    return OfferMapper
        .getDtoFromEntity(offerRepository
            .getOne(id));
  }

  List<OfferReturnDto> getOffers() {
    return offerRepository
        .findAll()
        .stream()
        .map(e -> OfferMapper.getReturnDtoFromEntity(e, "user"))
        .collect(Collectors.toList());
  }

  OfferDto saveOfferDto(OfferDto dto) {
    Offer def = new Offer(
        dto.getLoanAmount(),
        dto.getTimePeriod(),
        dto.getInterestRate(),
        dto.getEndDate(),
        dto.getUser()
    );

    offerRepository.save(def);
    return getOffer(def.getId());
  }

  OfferDto updateOfferDto(OfferDto dto) {
    if(dto.getId() != null) {
      Offer offer = offerRepository.getOne(dto.getId());
      offer = new Offer(
          offer.getId(),
          offer.getLoanAmount(),
          offer.getTimePeriod(),
          offer.getInterestRate(),
          offer.getEndDate(),
          offer.getUser()
      );
      offerRepository.save(offer);
      return getOffer(offer.getId());
    } else {
      throw new IllegalArgumentException("Object cannot be updated, id is null");
    }
  }
}
