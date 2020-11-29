package pl.fintech.dragons.dragonslending.sociallending.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;

import java.time.LocalDate;

@RequiredArgsConstructor
@Transactional
public class AuctionTerminator {

    private final AuctionRepository auctionRepository;
    private final EventPublisher eventPublisher;

    @Scheduled(cron = "0 0 2 * * *")
    public void handleTimeToSelectOfferForAuctionEnded() {
        auctionRepository.findAllByAuctionStatusAndEndDateBefore(AuctionStatus.ACTIVE, LocalDate.now())
                .forEach(auction -> {
                    auction.makeAuctionTerminated();
                    eventPublisher.publish(AuctionTerminated.now(auction.getUserId(), auction.getId()));
                });
    }
}
