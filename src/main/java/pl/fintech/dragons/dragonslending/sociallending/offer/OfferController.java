package pl.fintech.dragons.dragonslending.sociallending.offer;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fintech.dragons.dragonslending.common.HeaderUtil;
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferQueryDto;
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferRequest;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class OfferController {
  private final OfferService offerService;

  @Value("${pl.fintech.app-name}")
  private String applicationName;
  private static final String ENTITY_NAME = "offer";

  @Operation(summary = "Get list of all offers current logged user")
  @GetMapping("/offers/user")
  List<OfferQueryDto> getCurrentLoggedUserOffers() {
    log.debug("REST request to get list of current logged user offers");
    return offerService.getCurrentLoggedUserOffers();
  }

  @Operation(summary = "Get list of offers by auction id")
  @GetMapping("/offers/auction/{auctionId}")
  ResponseEntity<List<OfferQueryDto>> getOffersByAuctionId(@PathVariable UUID auctionId) {
    log.debug("REST request to get list of offers by auction id");
    return ResponseEntity.ok().body(offerService.getOffersByAuctionId(auctionId));
  }

  @Operation(summary = "Save offer")
  @PostMapping("/offers")
  ResponseEntity<UUID> createOffer(@RequestBody @Valid OfferRequest offerRequest) throws URISyntaxException {
    log.debug("REST request to create Offer : {}", offerRequest);
    UUID offerId = offerService.saveOffer(offerRequest);
    return ResponseEntity.created(new URI("/api/offers/" + offerId))
        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, offerId.toString()))
        .body(offerId);
  }

  @Operation(summary = "Delete offer by id")
  @DeleteMapping("/offers/{id}")
  ResponseEntity<Void> deleteOffer(@PathVariable UUID id) {
    log.debug("REST request to delete offer by id: {}", id);
    offerService.deleteOffer(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Select offer")
  @PostMapping("/offers/{offerId}/select")
  void selectOffer(@PathVariable UUID offerId) {
    log.debug("REST request to select Offer : {}", offerId);
    offerService.selectOffer(offerId);
  }

  @Operation(summary = "Get list of received offers")
  @GetMapping("/offers/received")
  List<OfferQueryDto> getAllReceivedOffers() {
    log.debug("REST request to get list of received offers");
    return offerService.getListReceivedOffers();
  }
}
