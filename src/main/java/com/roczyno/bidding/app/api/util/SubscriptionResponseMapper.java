package com.roczyno.bidding.app.api.util;

import com.roczyno.bidding.app.api.model.Subscription;
import com.roczyno.bidding.app.api.response.SubscriptionResponse;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionResponseMapper {
	public SubscriptionResponse toSubscriptionResponse(Subscription req) {
		return new SubscriptionResponse(
				req.getSubscriptionStartDate(),
				req.getSubscriptionStartDate(),
				req.isSubscriptionValid(),
				req.getPlanType()
		);
	}

	public Subscription toSubscription(SubscriptionResponse subscriptionResponse) {
		return Subscription.builder()
				.subscriptionStartDate(subscriptionResponse.subscriptionStartDate())
				.subscriptionEndDate(subscriptionResponse.subscriptionEndDate())
				.isSubscriptionValid(subscriptionResponse.isSubscriptionValid())
				.planType(subscriptionResponse.planType())
				.build();
	}
}
