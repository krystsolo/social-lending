package pl.fintech.dragons.dragonslending.sociallending.auction;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fintech.dragons.dragonslending.common.HeaderUtil;
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto;
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionRequest;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
class AuctionController {
  private final AuctionService auctionService;

  @Value("${pl.fintech.app-name}")
  private String applicationName;
  private static final String ENTITY_NAME = "auction";

  @Operation(summary = "Get auction details by id")
  @GetMapping("/auctions/{id}")
  ResponseEntity<AuctionQueryDto> getAuctionById(@PathVariable UUID id) {
    log.debug("REST request to get Auction : {}", id);
    return ResponseEntity.ok()
        .body(auctionService.getAuction(id));
  }

  @Operation(summary = "Get list of auctions when user is logged in")
  @GetMapping("/auctions")
  ResponseEntity<List<AuctionQueryDto>> getAuctions(@RequestParam(required = false, defaultValue = "false") Boolean yours) {
    log.debug("REST request to get list of auctions when user is logged in");
    if(yours) {
      return ResponseEntity.ok().body(auctionService.getCurrentUserAuctions());
    } else {
      return ResponseEntity.ok().body(auctionService.getAllNotCurrentUserAuctions());
    }
  }

  @Operation(summary = "Get list of all auctions")
  @GetMapping("/auctions/public")
  ResponseEntity<List<AuctionQueryDto>> getPublicAuctions() {
    log.debug("REST request to get list of auctions");
    return ResponseEntity.ok().body(auctionService.getPublicAuctions());
  }

  @Operation(summary = "Save auction")
  @PostMapping("/auctions")
  ResponseEntity<UUID> createAuction(@RequestBody @Valid AuctionRequest auctionRequest) throws URISyntaxException {
    log.debug("REST request to create Auction : {}", auctionRequest);
    UUID auctionId = auctionService.saveAuction(auctionRequest);
    return ResponseEntity.created(new URI("/api/auctions/" + auctionId))
        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, auctionId.toString()))
        .body(auctionId);
  }

  @Operation(summary = "Update auction")
  @PutMapping("/auctions/{id}")
  ResponseEntity<UUID> updateAuction(@RequestBody @Valid AuctionRequest auctionRequest) throws AccessDeniedException {
    log.debug("REST request to update Auction : {}", auctionRequest);
    UUID auctionId = auctionService.updateAuction(auctionRequest);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, auctionId.toString()))
        .body(auctionId);
  }

  @Operation(summary = "Delete auction by id")
  @DeleteMapping("/auctions/{id}")
  ResponseEntity<Void> deleteAuction(@PathVariable UUID id) throws AccessDeniedException {
    log.debug("REST request to delete Auction by id: {}", id);
    auctionService.deleteAuction(id);
    return ResponseEntity.noContent().build();
  }
}
