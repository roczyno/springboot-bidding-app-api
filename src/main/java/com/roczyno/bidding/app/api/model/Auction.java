package com.roczyno.bidding.app.api.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Future;
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
    private String startingBid;
    private String buyNowPrice;
    private long currentBid;
    @ElementCollection
    private List<String> images;
    private LocalDateTime createdAt;
    @ManyToOne
    private User user;
    @ManyToOne
    private Bid bid;


}
