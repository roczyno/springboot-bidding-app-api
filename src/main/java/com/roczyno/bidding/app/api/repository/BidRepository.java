package com.roczyno.bidding.app.api.repository;

import com.roczyno.bidding.app.api.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Integer> {
}
