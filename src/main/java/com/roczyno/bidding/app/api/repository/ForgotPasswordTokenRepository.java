package com.roczyno.bidding.app.api.repository;


import com.roczyno.bidding.app.api.model.ForgotPasswordToken;
import com.roczyno.bidding.app.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Integer> {

    ForgotPasswordToken findByTokenAndUser(int token, User user);
}
