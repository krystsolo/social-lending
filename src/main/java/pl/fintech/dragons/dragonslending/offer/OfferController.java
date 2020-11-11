package pl.fintech.dragons.dragonslending.offer;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fintech.dragons.dragonslending.offer.dto.OfferDto;
import pl.fintech.dragons.dragonslending.offer.dto.OfferReturnDto;
import pl.fintech.dragons.dragonslending.utils.HeaderUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
class OfferController {
  private final OfferService offerService;

  @Value("${pl.fintech.app-name}")
  private String applicationName;
  private static final String ENTITY_NAME = "offer";

  @Operation(summary = "Get offer details by id")
  @GetMapping("/offers/{id}")
  OfferDto getOfferById(@PathVariable UUID id) {
    log.debug("REST request to get Offer : {}", id);
    return offerService.getOffer(id);
  }

  @Operation(summary = "Get list of offers")
  @GetMapping("/offers")
  List<OfferReturnDto> getOffers() {
    log.debug("REST request to get list of Offers");
    return offerService.getOffers();
  }

  @Operation(summary = "Save offer")
  @PostMapping("/offers")
  ResponseEntity<OfferDto> createOffer(@RequestBody @Valid OfferDto offerDto) throws URISyntaxException {
    log.debug("REST request to create Offer : {}", offerDto);
    OfferDto result = offerService.saveOfferDto(offerDto);
    return ResponseEntity.created(new URI("/api/offers/" + result.getId()))
        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
        .body(result);
  }

  @Operation(summary = "Update offer")
  @PutMapping("/offers/{id}")
  ResponseEntity<OfferDto> updateOfferDto(@RequestBody @Valid OfferDto offerDto) {
    log.debug("REST request to update Offer : {}", offerDto);
    OfferDto result = offerService.updateOfferDto(offerDto);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
        .body(result);
  }
}
