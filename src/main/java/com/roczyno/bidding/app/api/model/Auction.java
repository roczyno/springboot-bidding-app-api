package com.roczyno.bidding.app.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class Auction {
    @Id
    @GeneratedValue
    private Integer id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private long timeLeft;
    private String distanceCv;
    private String location;
    private String modelColor;
    private String transmission;
    private String engineType;
    private long startingBid;
    private long buyNowPrice;
    private long  currentBid;
    private long  activeBids=0;
    private AuctionStatus auctionStatus;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> images;
    private LocalDateTime createdAt;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "auction",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Bid> bid;


}
