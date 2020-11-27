package pl.fintech.dragons.dragonslending.sociallending.offer;


import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionService;
import pl.fintech.dragons.dragonslending.sociallending.auction.AuctionTerminated;
import pl.fintech.dragons.dragonslending.sociallending.auction.dto.AuctionQueryDto;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserDto;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.LoanCalculationService;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculation;
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.Calculation;
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferQueryDto;
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
public class OfferService {
    private final OfferRepository offerRepository;
    private final LoanCalculationService loanCalculationService;
    private final UserService userService;
    private final AuctionService auctionService;
    private final EventPublisher eventPublisher;

    public List<OfferQueryDto> getCurrentLoggedUserOffers() {
        return mapOfferListToDto(offerRepository
                .findAllByUserIdAndOfferStatus(
                        userService.getCurrentLoggedUser().getId(),
                        OfferStatus.ACTIVE));
    }

    public List<OfferQueryDto> getOffersByAuctionId(UUID auctionId) {
        return mapOfferListToDto(offerRepository
                .findAllByAuctionId(auctionId));
    }

    public UUID saveOffer(OfferRequest dto) {
        AuctionQueryDto auction = auctionService.getAuction(dto.getAuctionId());
        UserDto user = userService.getCurrentLoggedUser();

        if (auction == null || offerRepository.findByAuctionIdAndUserId(dto.getAuctionId(), user.getId()).isPresent()) {
            throw new IllegalArgumentException("You can't add an offer to this auction");
        }

        Offer offer = new Offer(
                dto.getOfferAmount(),
                dto.getInterestRate(),
                auction.getTimePeriod(),
                dto.getAuctionId(),
                user.getId()
        );

        offerRepository.save(offer);
        eventPublisher.publish(OfferSubmitted.now(auction.getId(), offer.getId(), user.getId(), offer.getOfferAmount()));
        return offer.getId();
    }

    public void deleteOffer(UUID offerId) throws AccessDeniedException {
        if (!userService.getCurrentLoggedUser().getId().equals(offerRepository.getOne(offerId).userId)) {
            throw new AccessDeniedException("You don't have permission to delete this offer");
        }

        Offer offer = offerRepository.getOne(offerId);
        offer.makeOfferTerminated();
        eventPublisher.publish(OfferTerminated.now(offer.getAuctionId(), offer.getId(), offer.getUserId(), offer.getOfferAmount()));
    }

    @EventListener
    public void handle(AuctionTerminated event) {
        offerRepository.findAllByAuctionId(event.getAuctionId())
                .forEach(offer -> {
                    offer.makeOfferTerminated();
                    eventPublisher.publish(OfferTerminated.now(offer.getAuctionId(), offer.getId(), offer.getUserId(), offer.getOfferAmount()));
                });
    }

    private List<OfferQueryDto> mapOfferListToDto(List<Offer> offers) {
        return offers
                .stream()
                .map(offer -> offer.toOfferDto(
                        userService.getUser(offer.getUserId()).getUsername(),
                        getCalculation(offer),
                        userService.getUser(auctionService.getAuction(offer.getAuctionId()).getUserId()).getUsername())
                ).collect(Collectors.toList());
    }

    private Calculation getCalculation(Offer offer) {
        LoanCalculation loanCalculation = loanCalculationService.calculateAmountsToRepaid(
                offer.getOfferAmount(),
                offer.getTimePeriod(),
                offer.getInterestRate());
        BigDecimal periodValue = loanCalculation.totalAmountToRepaid()
                .divide(BigDecimal.valueOf(offer.getTimePeriod()), 2, RoundingMode.HALF_UP);
        return new Calculation(
                loanCalculation.totalAmountToRepaid(),
                periodValue);
    }
}
