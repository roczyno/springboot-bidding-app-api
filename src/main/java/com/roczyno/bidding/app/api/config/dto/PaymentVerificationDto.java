package com.roczyno.bidding.app.api.config.dto;

import com.roczyno.bidding.app.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVerificationDto {

	private User user;
	private String reference;
	private BigDecimal amount;
	private String gatewayResponse;
	private String paidAt;
	private String createdAt;
	private String channel;
	private String currency;
	private String ipAddress;
	private String pricingPlanType;
	private Date createdOn = new Date();
}
