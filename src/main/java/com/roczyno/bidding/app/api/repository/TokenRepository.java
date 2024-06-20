package com.roczyno.bidding.app.api.repository;





import com.roczyno.bidding.app.api.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository  extends JpaRepository<Token,Integer> {
    Optional<Token> findByToken(String token);
}
