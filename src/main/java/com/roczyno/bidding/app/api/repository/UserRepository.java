package com.roczyno.bidding.app.api.repository;


import com.roczyno.bidding.app.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String userEmail);
}
