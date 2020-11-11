package pl.fintech.dragons.dragonslending.offer;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fintech.dragons.dragonslending.offer.dto.OfferRequest;
import pl.fintech.dragons.dragonslending.offer.dto.OfferQueryDto;
import pl.fintech.dragons.dragonslending.common.HeaderUtil;

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
  OfferQueryDto getOfferById(@PathVariable UUID id) {
    log.debug("REST request to get Offer : {}", id);
    return offerService.getOffer(id);
  }

  @Operation(summary = "Get list of offers")
  @GetMapping("/offers")
  List<OfferQueryDto> getOffers() {
    log.debug("REST request to get list of Offers");
    return offerService.getOffers();
  }

  @Operation(summary = "Save offer")
  @PostMapping("/offers")
  ResponseEntity<UUID> createOffer(@RequestBody @Valid OfferRequest offerRequest) throws URISyntaxException {
    log.debug("REST request to create Offer : {}", offerRequest);
    UUID offerId = offerService.saveOfferDto(offerRequest);
    return ResponseEntity.created(new URI("/api/offers/" + offerId))
        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, offerId.toString()))
        .body(offerId);
  }

  @Operation(summary = "Update offer")
  @PutMapping("/offers/{id}")
  ResponseEntity<UUID> updateOfferDto(@RequestBody @Valid OfferRequest offerRequest) throws IllegalAccessException {
    log.debug("REST request to update Offer : {}", offerRequest);
    UUID offerId = offerService.updateOfferDto(offerRequest);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, offerId.toString()))
        .body(offerId);
  }
}
