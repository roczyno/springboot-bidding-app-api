package com.roczyno.bidding.app.api.util;

import com.roczyno.bidding.app.api.model.Bid;
import com.roczyno.bidding.app.api.response.BidResponse;
import org.springframework.stereotype.Service;

@Service
public class BidMapper {
    public BidResponse toBidResponse(Bid bid) {
        return new BidResponse(
                bid.getId(),
                bid.getUser().getFirstName(),
                bid.getCreatedAt(),
                bid.getAmount()

        );
    }
}
