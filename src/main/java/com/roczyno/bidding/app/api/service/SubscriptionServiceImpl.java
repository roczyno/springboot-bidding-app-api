package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.PlanType;
import com.roczyno.bidding.app.api.model.Subscription;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl  implements SubscriptionService{
	private final SubscriptionRepository subscriptionRepository;
	@Override
	public Subscription createSubscription(User user) {

		var subscription =Subscription.builder()
				.user(user)
				.subscriptionStartDate(LocalDate.now())
				.subscriptionEndDate(LocalDate.now().plusDays(12))
				.planType(PlanType.BASIC)
				.build();
		return subscriptionRepository.save(subscription);
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
