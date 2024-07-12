package com.roczyno.bidding.app.api.repository;

import com.roczyno.bidding.app.api.model.Auction;
import com.roczyno.bidding.app.api.model.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
	int countByUserId(Integer userId);

	@Query("SELECT a FROM Auction a WHERE " +
			"(:status IS NULL OR a.auctionStatus = :status) AND " +
			"(:searchTerm IS NULL OR " +
			"LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
	Page<Auction> findByStatusAndSearchTerm(@Param("status") AuctionStatus status,
											@Param("searchTerm") String searchTerm,
											Pageable pageable);

}
