package com.roczyno.bidding.app.api.config.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InitializePaymentDto {

	private String amount;


	private String email;


	private String currency;


	private String plan;

	private String[] channels;
}
