package com.roczyno.bidding.app.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class Bid {
    @Id
    @GeneratedValue
    private Integer id;
    private long amount;
    @ManyToOne
    private User user;
    @ManyToOne
    private Auction auction;
    private LocalDateTime createdAt;

}
