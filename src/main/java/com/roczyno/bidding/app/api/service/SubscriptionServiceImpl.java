package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.SubscriptionException;
import com.roczyno.bidding.app.api.model.PlanType;
import com.roczyno.bidding.app.api.model.Subscription;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.AuctionRepository;
import com.roczyno.bidding.app.api.repository.SubscriptionRepository;
import com.roczyno.bidding.app.api.response.SubscriptionResponse;
import com.roczyno.bidding.app.api.util.SubscriptionResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

	private final SubscriptionRepository subscriptionRepository;

	private static final int BASIC_PLAN_DAYS = 12;
	private static final int STANDARD_PLAN_DAYS = 30;
	private static final int PREMIUM_PLAN_DAYS = 365;
	private final SubscriptionResponseMapper mapper;

	@Override
	@Transactional
	public SubscriptionResponse createSubscription(User user) {
		Subscription subscription = Subscription.builder()
				.user(user)
				.subscriptionStartDate(LocalDate.now())
				.subscriptionEndDate(LocalDate.now().plusDays(BASIC_PLAN_DAYS))
				.planType(PlanType.BASIC)
				.build();
		var savedSubscription= subscriptionRepository.save(subscription);
		return mapper.toSubscriptionResponse(savedSubscription);
	}

	@Override
	public SubscriptionResponse getUserSubscription(Integer userId) {
		Subscription subscription = subscriptionRepository.findByUserId(userId);
		if (subscription == null) {
			throw new SubscriptionException("Subscription not found");
		}

		if (!isValidSubscription(userId)) {
			subscription.setPlanType(PlanType.BASIC);
			subscription.setSubscriptionStartDate(LocalDate.now());
			subscription.setSubscriptionEndDate(LocalDate.now().plusDays(BASIC_PLAN_DAYS));
			subscription = subscriptionRepository.save(subscription);
		}
		return mapper.toSubscriptionResponse(subscription);
	}

	@Override
	public SubscriptionResponse upgradeSubscription(Integer userId, PlanType planType) {
		Subscription subscription = subscriptionRepository.findByUserId(userId);
		subscription.setPlanType(planType);
		subscription.setSubscriptionStartDate(LocalDate.now());

		int duration = switch (planType) {
			case BASIC -> BASIC_PLAN_DAYS;
			case STANDARD -> STANDARD_PLAN_DAYS;
			case PREMIUM -> PREMIUM_PLAN_DAYS;
		};
		subscription.setSubscriptionEndDate(LocalDate.now().plusDays(duration));
		var upgradedSubscription=subscriptionRepository.save(subscription);
		return mapper.toSubscriptionResponse(upgradedSubscription);
	}

	@Override
	public boolean isValidSubscription(Integer userId) {
		Subscription subscription = subscriptionRepository.findByUserId(userId);
		LocalDate endDate = subscription.getSubscriptionEndDate();
		return !endDate.isBefore(LocalDate.now());
	}
}
