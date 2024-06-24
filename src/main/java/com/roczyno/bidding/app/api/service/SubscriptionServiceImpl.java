package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.PlanType;
import com.roczyno.bidding.app.api.model.Subscription;
import com.roczyno.bidding.app.api.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SubscriptionServiceImpl  implements SubscriptionService{
	@Override
	public Subscription createSubscription(User user) {

		Subscription.builder()
				.user(user)
				.subscriptionStartDate(LocalDate.now())
				.subscriptionEndDate(LocalDate.now().plusDays(12))
				.planType(PlanType.BASIC)
				.build();
		return null;
	}

	@Override
	public Subscription getUserSubscription(Long userId) {
		return null;
	}

	@Override
	public Subscription upgradeSubscription(Long userId, PlanType planType) {
		return null;
	}

	@Override
	public boolean isValidSubscription(Subscription subscription) {
		return false;
	}
}
