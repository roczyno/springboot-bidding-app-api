package com.roczyno.bidding.app.api.util;

public class AppConstants {

	public static final String CANNOT_BE_NULL="This field cannot be null";
	public static final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,}$";
	public static final String PASSWORD_PATTERN_MESSAGE = "Password must be at least 8 characters long and include at" +
			" least one digit, one uppercase letter, one lowercase letter, and one special character";
	public static final String USERNAME_PATTERN= "^[a-zA-Z][a-zA-Z0-9_]*$";
	public static final String USERNAME_PATTERN_MESSAGE="name must start with a letter and can only contain " +
			"letters, numbers, and underscores";
	public static final String EMAIL="Please provide a valid email address";
	public static final String NAME_SIZE_MESSAGE = "Username must be between 3 and 20 characters long";
	public static final String PHONE_PATTERN="^\\+?\\d{1,3}?[-. ]?\\(?\\d{1,3}?\\)?[-. ]?\\d{1,3}[-. ]?\\d{1,4}$";
	public static final String PHONE_PATTERN_MESSAGE="Invalid phone number";
}
