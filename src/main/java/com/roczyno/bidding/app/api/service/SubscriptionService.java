package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.PlanType;
import com.roczyno.bidding.app.api.model.Subscription;
import com.roczyno.bidding.app.api.model.User;
import org.springframework.security.core.Authentication;

public interface SubscriptionService {
	Subscription createSubscription(User user);
	Subscription getUserSubscription(Long userId);
	Subscription upgradeSubscription(Long userId, PlanType planType);
	boolean isValidSubscription(Subscription subscription);
}
