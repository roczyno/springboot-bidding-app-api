package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.SubscriptionException;
import com.roczyno.bidding.app.api.model.PlanType;
import com.roczyno.bidding.app.api.model.Subscription;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.AuctionRepository;
import com.roczyno.bidding.app.api.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

	private final SubscriptionRepository subscriptionRepository;

	private static final int BASIC_PLAN_DAYS = 12;
	private static final int STANDARD_PLAN_DAYS = 30;
	private static final int PREMIUM_PLAN_DAYS = 365;
	private final AuctionRepository auctionRepository;

	@Override
	public Subscription createSubscription(User user) {
		Subscription subscription = Subscription.builder()
				.user(user)
				.subscriptionStartDate(LocalDate.now())
				.subscriptionEndDate(LocalDate.now().plusDays(BASIC_PLAN_DAYS))
				.planType(PlanType.BASIC)
				.build();
		return subscriptionRepository.save(subscription);
	}

	@Override
	public Subscription getUserSubscription(Integer userId) {
		Subscription subscription = subscriptionRepository.findByUserId(userId);
		if (subscription == null) {
			throw new SubscriptionException("Subscription not found");
		}

		if (!isValidSubscription(subscription)) {
			subscription.setPlanType(PlanType.BASIC);
			subscription.setSubscriptionStartDate(LocalDate.now());
			subscription.setSubscriptionEndDate(LocalDate.now().plusDays(BASIC_PLAN_DAYS));
			subscription = subscriptionRepository.save(subscription);
		}
		return subscription;
	}

	@Override
	public Subscription upgradeSubscription(Integer userId, PlanType planType) {
		Subscription subscription = getUserSubscription(userId);
		subscription.setPlanType(planType);
		subscription.setSubscriptionStartDate(LocalDate.now());

		int duration = switch (planType) {
			case BASIC -> BASIC_PLAN_DAYS;
			case STANDARD -> STANDARD_PLAN_DAYS;
			case PREMIUM -> PREMIUM_PLAN_DAYS;
		};
		subscription.setSubscriptionEndDate(LocalDate.now().plusDays(duration));
		return subscriptionRepository.save(subscription);
	}

	@Override
	public boolean isValidSubscription(Subscription subscription) {
		LocalDate endDate = subscription.getSubscriptionEndDate();
		return !endDate.isBefore(LocalDate.now());
	}
}
