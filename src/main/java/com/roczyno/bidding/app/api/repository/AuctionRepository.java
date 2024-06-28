package com.roczyno.bidding.app.api.repository;

import com.roczyno.bidding.app.api.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
	int countByUserId(Integer userId);
}
