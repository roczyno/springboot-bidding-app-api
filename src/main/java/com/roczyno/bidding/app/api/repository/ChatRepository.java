package com.roczyno.bidding.app.api.repository;

import com.roczyno.bidding.app.api.model.Chat;
import com.roczyno.bidding.app.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
	@Query("select c from Chat c where :user1 member of c.users and :user2 member of c.users")
	Chat findSingleChatByUserIds(@Param("user1") User user1, @Param("user2") User user2);

	@Query("select c from Chat c join c.users u where u.id=:userId ")
	List<Chat> findByUserId(@Param("userId") Integer userId);
}
