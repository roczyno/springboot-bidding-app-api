package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.SubscriptionException;
import com.roczyno.bidding.app.api.model.PlanType;
import com.roczyno.bidding.app.api.model.Subscription;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.SubscriptionRepository;
import com.roczyno.bidding.app.api.response.SubscriptionResponse;
import com.roczyno.bidding.app.api.util.SubscriptionResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service implementation for handling user subscriptions, including creation, retrieval, and upgrades.
 */
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

	private final SubscriptionRepository subscriptionRepository;
	private final SubscriptionResponseMapper mapper;

	private static final int BASIC_PLAN_DAYS = 12;
	private static final int STANDARD_PLAN_DAYS = 30;
	private static final int PREMIUM_PLAN_DAYS = 365;

	/**
	 * Creates a new subscription for a user with the BASIC plan by default.
	 *
	 * @param user the user to create a subscription for.
	 */
	@Override
	@Transactional
	public void createSubscription(User user) {
		Subscription subscription = Subscription.builder()
				.user(user)
				.subscriptionStartDate(LocalDate.now())
				.subscriptionEndDate(LocalDate.now().plusDays(BASIC_PLAN_DAYS))
				.planType(PlanType.BASIC)
				.build();
		subscriptionRepository.save(subscription);
	}

	/**
	 * Retrieves a user's subscription. If the subscription is expired, it is reset to a BASIC plan.
	 *
	 * @param userId the ID of the user.
	 * @return the current subscription as a SubscriptionResponse.
	 * @throws SubscriptionException if no subscription is found for the user.
	 */
	@Override
	public SubscriptionResponse getUserSubscription(Integer userId) {
		Subscription subscription = subscriptionRepository.findByUserId(userId);
		if (subscription == null) {
			throw new SubscriptionException("Subscription not found for user with ID: " + userId);
		}

		// Reset subscription if expired
		if (!isValidSubscription(subscription)) {
			subscription.setPlanType(PlanType.BASIC);
			subscription.setSubscriptionStartDate(LocalDate.now());
			subscription.setSubscriptionEndDate(LocalDate.now().plusDays(BASIC_PLAN_DAYS));
			subscription = subscriptionRepository.save(subscription);
		}
		return mapper.toSubscriptionResponse(subscription);
	}

	/**
	 * Upgrades a user's subscription to a specified plan.
	 *
	 * @param userId   the ID of the user.
	 * @param planType the type of the plan to upgrade to (BASIC, STANDARD, PREMIUM).
	 * @return the updated subscription as a SubscriptionResponse.
	 * @throws SubscriptionException if the subscription is not found for the user.
	 */
	@Override
	@Transactional
	public SubscriptionResponse upgradeSubscription(Integer userId, PlanType planType) {
		Subscription subscription = subscriptionRepository.findByUserId(userId);
		if (subscription == null) {
			throw new SubscriptionException("Subscription not found for user with ID: " + userId);
		}

		subscription.setPlanType(planType);
		subscription.setSubscriptionStartDate(LocalDate.now());

		// Set the subscription end date based on the plan type
		int duration = switch (planType) {
			case BASIC -> BASIC_PLAN_DAYS;
			case STANDARD -> STANDARD_PLAN_DAYS;
			case PREMIUM -> PREMIUM_PLAN_DAYS;
		};

		subscription.setSubscriptionEndDate(LocalDate.now().plusDays(duration));
		subscription = subscriptionRepository.save(subscription);
		return mapper.toSubscriptionResponse(subscription);
	}

	/**
	 * Checks if a user's subscription is still valid.
	 *
	 * @param userId the ID of the user.
	 * @return true if the subscription is valid, false otherwise.
	 */
	@Override
	public boolean isValidSubscription(Integer userId) {
		Subscription subscription = subscriptionRepository.findByUserId(userId);
		if (subscription == null) {
			throw new SubscriptionException("Subscription not found for user with ID: " + userId);
		}
		return isValidSubscription(subscription);
	}

	/**
	 * Helper method to check if the given subscription is still valid.
	 *
	 * @param subscription the subscription to check.
	 * @return true if the subscription is valid, false if it is expired.
	 */
	private boolean isValidSubscription(Subscription subscription) {
		LocalDate endDate = subscription.getSubscriptionEndDate();
		return !endDate.isBefore(LocalDate.now());
	}
}
