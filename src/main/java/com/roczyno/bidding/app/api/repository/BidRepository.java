package com.roczyno.bidding.app.api.repository;

import com.roczyno.bidding.app.api.model.Bid;
import com.roczyno.bidding.app.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Integer> {
    List<Bid> findByAuctionId(Integer auctionId);

	boolean existsByUserAndAuctionId(User user, Integer auctionId);

	int countByUserId(Integer userId);
}
