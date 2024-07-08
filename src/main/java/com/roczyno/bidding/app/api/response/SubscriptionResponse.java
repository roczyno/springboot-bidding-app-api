package com.roczyno.bidding.app.api.response;

import com.roczyno.bidding.app.api.model.PlanType;

import java.time.LocalDate;

public record SubscriptionResponse(
		 LocalDate subscriptionStartDate,
		 LocalDate subscriptionEndDate,
		 boolean isSubscriptionValid,
		 PlanType planType
) {
}
