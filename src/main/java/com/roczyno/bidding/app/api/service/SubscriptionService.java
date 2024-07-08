package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.PlanType;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.response.SubscriptionResponse;

public interface SubscriptionService {
	SubscriptionResponse createSubscription(User user);
	SubscriptionResponse getUserSubscription(Integer userId);
	SubscriptionResponse upgradeSubscription(Integer userId, PlanType planType);
	boolean isValidSubscription(Integer userId);
}
