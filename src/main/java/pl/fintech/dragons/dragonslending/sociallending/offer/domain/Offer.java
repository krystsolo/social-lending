package pl.fintech.dragons.dragonslending.sociallending.offer.domain;

import lombok.*;
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.Calculation;
import pl.fintech.dragons.dragonslending.sociallending.offer.dto.OfferQueryDto;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "offer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Offer {

  @Id
  private UUID id;

  @NotNull
  @DecimalMin(value = "0", message = "Offer amount should not be less than 0")
  @DecimalMax(value = "10000", message = "Offer amount should not be greater than 10000")
  @Column(name = "amount")
  private BigDecimal offerAmount;

  @NotNull
  @Min(value = 0, message = "Interest rate should not be less than 0")
  @Max(value = 20, message = "Interest rate should not be greater than 20")
  @Column(name = "interest_rate")
  private Float interestRate;

  @NotNull
  @Min(value = 1, message = "Time period value should not be less than 1")
  @Max(value = 36, message = "Time period value should not be greater than 36")
  @Column(name = "time_period")
  private Integer timePeriod;

  @Column(name = "auction_id")
  private UUID auctionId;

  @Column(name = "user_id")
  UUID userId;

  @Column(name = "status_offer")
  @Enumerated(EnumType.STRING)
  private OfferStatus offerStatus;

  public Offer(BigDecimal offerAmount, Float interestRate, Integer timePeriod, UUID auctionId, UUID userId) {
    this.id = UUID.randomUUID();
    this.offerAmount = offerAmount;
    this.interestRate = interestRate;
    this.timePeriod = timePeriod;
    this.auctionId = auctionId;
    this.userId = userId;
    this.offerStatus = OfferStatus.ACTIVE;
  }

  public void makeOfferTerminated() {
    this.offerStatus = OfferStatus.TERMINATED;
  }

  public OfferQueryDto toOfferDto(String username, Calculation calculation, String auctionOwner) {
    return OfferQueryDto.builder()
        .id(this.id)
        .offerAmount(this.offerAmount)
        .interestRate(this.interestRate)
        .calculation(calculation)
        .userId(this.userId)
        .auctionId(this.auctionId)
        .auctionOwner(auctionOwner)
        .username(username)
        .build();
  }

  public boolean isActive() {
    return offerStatus == OfferStatus.ACTIVE;
  }
}
